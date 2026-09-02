"""线索相关 Schema。"""
from datetime import datetime

from pydantic import BaseModel, ConfigDict


class LeadBase(BaseModel):
    name: str
    company: str | None = None
    phone: str | None = None
    email: str | None = None
    source: str | None = None
    tags: str | None = None
    status: str = "pending"
    owner_id: int | None = None
    remark: str | None = None


class LeadCreate(LeadBase):
    pass


class LeadUpdate(BaseModel):
    name: str | None = None
    company: str | None = None
    phone: str | None = None
    email: str | None = None
    source: str | None = None
    tags: str | None = None
    status: str | None = None
    owner_id: int | None = None
    remark: str | None = None


class LeadOut(LeadBase):
    model_config = ConfigDict(from_attributes=True)

    id: int
    created_at: datetime
    updated_at: datetime


class LeadBatchAssign(BaseModel):
    lead_ids: list[int]
    owner_id: int
