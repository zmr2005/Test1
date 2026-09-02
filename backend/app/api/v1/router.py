"""API v1 路由聚合。"""
from fastapi import APIRouter

from app.api.v1.endpoints import customers, leads, opportunities, tasks

api_router = APIRouter()
api_router.include_router(leads.router, prefix="/leads", tags=["leads"])
api_router.include_router(customers.router, prefix="/customers", tags=["customers"])
api_router.include_router(opportunities.router, prefix="/opportunities", tags=["opportunities"])
api_router.include_router(tasks.router, prefix="/tasks", tags=["tasks"])
