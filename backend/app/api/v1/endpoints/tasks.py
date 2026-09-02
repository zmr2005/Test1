"""任务 / 待办 / 日报端点（骨架）。"""
from fastapi import APIRouter, HTTPException, Query, status
from sqlalchemy import select

from app.api.deps import CurrentUser, DbSession
from app.models.task import Task
from app.schemas.task import TaskCreate, TaskOut

router = APIRouter()


@router.get("", response_model=list[TaskOut])
def list_tasks(
    db: DbSession,
    user_id: CurrentUser,
    skip: int = Query(0, ge=0),
    limit: int = Query(20, ge=1, le=100),
    type: str | None = None,
    done: bool | None = None,
):
    stmt = select(Task).where(Task.owner_id == user_id)
    if type:
        stmt = stmt.where(Task.type == type)
    if done is not None:
        stmt = stmt.where(Task.done == done)
    return list(db.scalars(stmt.offset(skip).limit(limit)).all())


@router.post("", response_model=TaskOut, status_code=status.HTTP_201_CREATED)
def create_task(payload: TaskCreate, db: DbSession, user_id: CurrentUser):
    task = Task(**payload.model_dump(), owner_id=user_id)
    db.add(task)
    db.commit()
    db.refresh(task)
    return task


@router.put("/{task_id}/done", response_model=TaskOut)
def toggle_done(task_id: int, done: bool, db: DbSession):
    task = db.get(Task, task_id)
    if not task:
        raise HTTPException(status_code=404, detail="任务不存在")
    task.done = done
    db.commit()
    db.refresh(task)
    return task
