"""客户端点（骨架，含公海回收/认领入口占位）。"""
from fastapi import APIRouter, HTTPException, Query, status
from sqlalchemy import select

from app.api.deps import CurrentUser, DbSession
from app.models.customer import Customer
from app.schemas.customer import CustomerCreate, CustomerOut

router = APIRouter()


@router.get("", response_model=list[CustomerOut])
def list_customers(
    db: DbSession,
    skip: int = Query(0, ge=0),
    limit: int = Query(20, ge=1, le=100),
    status: str | None = None,
):
    stmt = select(Customer)
    if status:
        stmt = stmt.where(Customer.status == status)
    return list(db.scalars(stmt.offset(skip).limit(limit)).all())


@router.post("", response_model=CustomerOut, status_code=status.HTTP_201_CREATED)
def create_customer(payload: CustomerCreate, db: DbSession, user_id: CurrentUser):
    customer = Customer(**payload.model_dump(), owner_id=user_id)
    db.add(customer)
    db.commit()
    db.refresh(customer)
    return customer


@router.get("/{customer_id}", response_model=CustomerOut)
def get_customer(customer_id: int, db: DbSession):
    customer = db.get(Customer, customer_id)
    if not customer:
        raise HTTPException(status_code=404, detail="客户不存在")
    return customer


@router.post("/{customer_id}/reclaim", response_model=dict)
def reclaim_customer(customer_id: int, db: DbSession, user_id: CurrentUser):
    """从公海认领客户（TODO: 需加并发锁防止多人同时认领）。"""
    customer = db.get(Customer, customer_id)
    if not customer:
        raise HTTPException(status_code=404, detail="客户不存在")
    if customer.status != "public":
        raise HTTPException(status_code=400, detail="仅公海客户可认领")
    customer.status = "private"
    customer.owner_id = user_id
    db.commit()
    return {"ok": True}
