from fastapi.middleware.cors import CORSMiddleware
from fastapi import FastAPI, Path, HTTPException
from sqlalchemy.orm import sessionmaker
from sqlalchemy import create_engine
import sqlalchemy.orm as sqlorm
from database import *
from classes import *

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Разрешить доступ с любых доменов
    allow_credentials=True,
    allow_methods=["*"],  # Разрешить все методы (GET, POST, PUT и т.д.)
    allow_headers=["*"],  # Разрешить все заголовки
)

engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = sqlorm.declarative_base()


def get_theme_by_name(name):
    return SessionLocal().query(Theme).filter(Theme.name == name).first()

def get_theme_by_id(id):
    return SessionLocal().query(Theme).filter(Theme.id == id).first()

def get_task_by_name_in_theme(id, name):
    return SessionLocal().query(Task).filter(Task.theme_id == id, Task.name == name).first()


@app.post("/create_theme", tags=["Theme"])
async def theme_creating(theme_request: ThemeRequest):
    if get_theme_by_name(theme_request.name):
        return {"message": "Такая тема уже есть!"}
    else:
        db = SessionLocal()
        new_theme = Theme(
            name=theme_request.name
        )
        db.add(new_theme)
        db.commit()
        db.refresh(new_theme)
        db.close()
        return {"message": "Тема успешно добавлена", "data": theme_request}


@app.put("/rename_theme/{id}", tags=["Theme"])
async def theme_renaming(
        id: int = Path(..., description="ID темы для изменения"),
        theme_request: ThemeRequest = None
):
    db = SessionLocal()
    db_theme = db.query(Theme).filter(Theme.id == id).first()
    if not db_theme:
        db.close()
        raise HTTPException(status_code=404, detail="Тема не найдена")
    if get_theme_by_name(theme_request.name):
        db.close()
        return {"message": "Тема с таким названием уже существует!"}
    db_theme.name = theme_request.name
    db.commit()
    db.refresh(db_theme)
    db.close()
    return {"message": "Тема успешно переименована", "data": theme_request}


@app.delete("/delete_theme/{id}", tags=["Theme"])
async def theme_deleting(
        id: int = Path(..., description="ID темы для удаления")
):
    db = SessionLocal()
    db_theme = db.query(Theme).filter(Theme.id == id).first()
    if not db_theme:
        db.close()
        raise HTTPException(status_code=404, detail="Тема не найдена")
    db.delete(db_theme)
    db.commit()
    db.close()
    return {"message": "Тема и все связанные задачи успешно удалены"}


@app.post("/create_task/{theme_id}", tags=["Task"])
async def task_creating(
    theme_id: int = Path(..., description="ID темы в которую мы добавляем задачу"),
    task_request: TaskRequest = None
):
    if get_task_by_name_in_theme(theme_id, task_request.name):
        return {"message": "Такая задача в теме уже есть!"}
    db = SessionLocal()
    new_task = Task(
        theme_id=theme_id,
        name=task_request.name,
        description=task_request.description,
        status=task_request.status,
    )
    db.add(new_task)
    db.commit()
    db.refresh(new_task)
    db.close()
    return {"message": "Задача успешно добавлена", "data": new_task}
    

@app.put("/edit_task/{id}", tags=["Task"])
async def task_editing(
        id: int = Path(..., description="ID задачи для изменения"),
        task_edit_request: EditTasks = None
):
    db = SessionLocal()
    db_task = db.query(Task).filter(Task.id == id).first()
    if not db_task:
        db.close()
        raise HTTPException(status_code=404, detail="Задача не найдена")
    if get_task_by_name_in_theme(db_task.theme_id, task_edit_request.name):
        db.close()
        return {"message": "Такая задача в теме уже есть!"}
    fields_to_update = {}
    if task_edit_request.name is not None: fields_to_update['name'] = task_edit_request.name
    else: fields_to_update['name'] = db_task.name
    if task_edit_request.description is not None: fields_to_update['description'] = task_edit_request.description
    else: fields_to_update['description'] = db_task.description
    if task_edit_request.status is not None: fields_to_update['status'] = task_edit_request.status
    else: fields_to_update['status'] = db_task.status
    for field, value in fields_to_update.items():
        setattr(db_task, field, value)
    db.commit()
    db.close()

    return {"message": "Задача успешно изменена", "data": task_edit_request}


@app.delete("/delete_task/{id}", tags=["Task"])
async def task_deleting(
        id: int = Path(..., description="ID задачи для удаления")
):
    db = SessionLocal()
    db_task = db.query(Task).filter(Task.id == id).first()
    if not db_task:
        db.close()
        raise HTTPException(status_code=404, detail="Задача не найдена")
    db.delete(db_task)
    db.commit()
    db.close()
    return {"message": "Задача успешно удалена"}


@app.get("/get_my_todo", tags=["All"])
async def get_list():
    return "OK"


@app.put("/reload_todo", tags=["All"])
async def change_list(todo_list_request: TodoListFormat):
    return "OK"
