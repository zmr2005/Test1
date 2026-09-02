"""线索端点（完整 CRUD + 查重 + 批量分配 + 转化）。"""
from fastapi import APIRouter, HTTPException, Query, status
from sqlalchemy import select
from sqlalchemy.orm import Session

from app.api.deps import CurrentUser, DbSession
from app.models.lead import Lead
from app.schemas.common import Page
from app.schemas.lead import LeadBatchAssign, LeadCreate, LeadOut, LeadUpdate
from app.services import lead_service

router = APIRouter()


@router.get("", response_model=Page[LeadOut])
def list_leads(
    db: DbSession,
    skip: int = Query(0, ge=0),
    limit: int = Query(20, ge=1, le=100),
    status: str | None = None,
    source: str | None = None,
    keyword: str | None = None,
):
    stmt = select(Lead)
    if status:
        stmt = stmt.where(Lead.status == status)
    if source:
        stmt = stmt.where(Lead.source == source)
    if keyword:
        stmt = stmt.where(Lead.name.contains(keyword) | Lead.company.contains(keyword))

    total = len(db.scalars(stmt).all())
    items = db.scalars(stmt.offset(skip).limit(limit)).all()
    return {"items": items, "total": total}


@router.post("", response_model=LeadOut, status_code=status.HTTP_201_CREATED)
def create_lead(payload: LeadCreate, db: DbSession, _: CurrentUser):
    # 入池前查重提示
    dups = lead_service.find_duplicates(db, payload.phone, payload.email, payload.company)
    lead = Lead(**payload.model_dump())
    db.add(lead)
    db.commit()
    db.refresh(lead)
    return lead


@router.get("/{lead_id}", response_model=LeadOut)
def get_lead(lead_id: int, db: DbSession):
    lead = db.get(Lead, lead_id)
    if not lead:
        raise HTTPException(status_code=404, detail="线索不存在")
    return lead


@router.put("/{lead_id}", response_model=LeadOut)
def update_lead(lead_id: int, payload: LeadUpdate, db: DbSession):
    lead = db.get(Lead, lead_id)
    if not lead:
        raise HTTPException(status_code=404, detail="线索不存在")
    for field, value in payload.model_dump(exclude_unset=True).items():
        setattr(lead, field, value)
    db.commit()
    db.refresh(lead)
    return lead


@router.delete("/{lead_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_lead(lead_id: int, db: DbSession):
    lead = db.get(Lead, lead_id)
    if not lead:
        raise HTTPException(status_code=404, detail="线索不存在")
    # 简化：移入回收站（软删除）；硬删除可改为 db.delete(lead)
    lead.status = "recycled"
    db.commit()


@router.post("/batch-assign", response_model=dict)
def batch_assign(payload: LeadBatchAssign, db: DbSession):
    updated = lead_service.batch_assign(db, payload.lead_ids, payload.owner_id)
    return {"updated": updated}


@router.post("/{lead_id}/convert", response_model=dict)
def convert_lead(lead_id: int, db: DbSession):
    lead = db.get(Lead, lead_id)
    if not lead:
        raise HTTPException(status_code=404, detail="线索不存在")
    customer = lead_service.convert_to_customer(db, lead)
    return {"customer_id": customer.id}
