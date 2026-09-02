"""任务 / 待办 / 工作日报模型。"""
from datetime import datetime

from sqlalchemy import Boolean, DateTime, String, Text
from sqlalchemy.orm import Mapped, mapped_column

from app.core.database import Base
from app.models.base import TimestampMixin

# 任务类型：日报 / 待办 / 拓客任务
TASK_TYPE = ("daily", "todo", "prospect")


class Task(Base, TimestampMixin):
    __tablename__ = "tasks"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    title: Mapped[str] = mapped_column(String(255), nullable=False)
    type: Mapped[str] = mapped_column(String(16), default="todo")
    content: Mapped[str | None] = mapped_column(Text, comment="日报内容/任务详情")
    owner_id: Mapped[int | None] = mapped_column(index=True)
    # 关联对象：可挂到线索/客户/商机
    related_type: Mapped[str | None] = mapped_column(String(32))
    related_id: Mapped[int | None] = mapped_column()
    due_at: Mapped[datetime | None] = mapped_column(DateTime, comment="截止时间")
    done: Mapped[bool] = mapped_column(Boolean, default=False)
