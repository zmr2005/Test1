"""ORM 模型导出，供 Alembic 与全局导入使用。"""
from app.models.lead import Lead
from app.models.customer import Customer, Contact
from app.models.opportunity import Opportunity
from app.models.task import Task

__all__ = ["Lead", "Customer", "Contact", "Opportunity", "Task"]
