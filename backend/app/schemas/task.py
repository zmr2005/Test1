"""任务相关 Schema。"""
from datetime import datetime

from pydantic import BaseModel, ConfigDict


class TaskCreate(BaseModel):
    title: str
    type: str = "todo"
    content: str | None = None
    related_type: str | None = None
    related_id: int | None = None
    due_at: datetime | None = None


class TaskOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    title: str
    type: str
    content: str | None = None
    owner_id: int | None = None
    related_type: str | None = None
    related_id: int | None = None
    due_at: datetime | None = None
    done: bool
    created_at: datetime
    updated_at: datetime
