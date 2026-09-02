"""商机相关 Schema。"""
from datetime import date, datetime

from pydantic import BaseModel, ConfigDict


class OpportunityCreate(BaseModel):
    customer_id: int
    name: str
    product: str | None = None
    budget: float | None = None
    stage: str = "contact"
    expected_close_date: date | None = None
    owner_id: int | None = None
    remark: str | None = None


class OpportunityOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    customer_id: int
    name: str
    product: str | None = None
    budget: float | None = None
    stage: str
    expected_close_date: date | None = None
    owner_id: int | None = None
    remark: str | None = None
    created_at: datetime
    updated_at: datetime
