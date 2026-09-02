"""商机端点（骨架）。"""
from fastapi import APIRouter, HTTPException, Query, status
from sqlalchemy import select

from app.api.deps import DbSession
from app.models.opportunity import Opportunity
from app.schemas.opportunity import OpportunityCreate, OpportunityOut

router = APIRouter()


@router.get("", response_model=list[OpportunityOut])
def list_opportunities(
    db: DbSession,
    skip: int = Query(0, ge=0),
    limit: int = Query(20, ge=1, le=100),
    stage: str | None = None,
):
    stmt = select(Opportunity)
    if stage:
        stmt = stmt.where(Opportunity.stage == stage)
    return list(db.scalars(stmt.offset(skip).limit(limit)).all())


@router.post("", response_model=OpportunityOut, status_code=status.HTTP_201_CREATED)
def create_opportunity(payload: OpportunityCreate, db: DbSession):
    opp = Opportunity(**payload.model_dump())
    db.add(opp)
    db.commit()
    db.refresh(opp)
    return opp


@router.get("/{opp_id}", response_model=OpportunityOut)
def get_opportunity(opp_id: int, db: DbSession):
    opp = db.get(Opportunity, opp_id)
    if not opp:
        raise HTTPException(status_code=404, detail="商机不存在")
    return opp


@router.put("/{opp_id}/stage", response_model=OpportunityOut)
def update_stage(opp_id: int, stage: str, db: DbSession):
    """推进商机阶段（TODO: 校验阶段合法性并记录流转日志）。"""
    opp = db.get(Opportunity, opp_id)
    if not opp:
        raise HTTPException(status_code=404, detail="商机不存在")
    opp.stage = stage
    db.commit()
    db.refresh(opp)
    return opp
