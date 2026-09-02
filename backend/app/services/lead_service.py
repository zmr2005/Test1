"""线索业务逻辑：查重、批量分配、转化。"""
from sqlalchemy import or_, select
from sqlalchemy.orm import Session

from app.models.customer import Customer
from app.models.lead import Lead


def find_duplicates(db: Session, phone: str | None, email: str | None, company: str | None) -> list[Lead]:
    """按手机号 / 邮箱 / 公司名查重，返回疑似重复线索。"""
    conditions = []
    if phone:
        conditions.append(Lead.phone == phone)
    if email:
        conditions.append(Lead.email == email)
    if company:
        conditions.append(Lead.company == company)
    if not conditions:
        return []
    stmt = select(Lead).where(or_(*conditions))
    return list(db.scalars(stmt).all())


def batch_assign(db: Session, lead_ids: list[int], owner_id: int) -> int:
    """批量分配线索给负责人，返回影响行数。"""
    if not lead_ids:
        return 0
    updated = (
        db.query(Lead)
        .filter(Lead.id.in_(lead_ids))
        .update({Lead.owner_id: owner_id, Lead.status: "assigned"}, synchronize_session=False)
    )
    db.commit()
    return updated


def convert_to_customer(db: Session, lead: Lead) -> Customer:
    """将线索转化为客户，并更新线索状态。"""
    customer = Customer(
        name=lead.name,
        phone=lead.phone,
        email=lead.email,
        owner_id=lead.owner_id,
        remark=lead.remark,
    )
    db.add(customer)
    db.flush()  # 取得 customer.id
    lead.status = "converted"
    db.commit()
    db.refresh(customer)
    return customer
