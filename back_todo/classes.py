from sqlalchemy import create_engine, Column, Integer, String, ForeignKey, Enum
from sqlalchemy.ext.declarative import declarative_base
from pydantic import BaseModel, Field, RootModel
from sqlalchemy.orm import relationship
from typing import Optional
from typing import Dict
from database import *
import enum

Base = declarative_base()

class StatusEnum(str, enum.Enum):
    PLAN = "Планируется"
    IN_PROGRESS = "Выполняется"
    DONE = "Готово"

class Theme(Base):
    __tablename__ = 'themes'
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(255), nullable=False)
    tasks = relationship('Task', back_populates='theme', cascade="all, delete")

class Task(Base):
    __tablename__ = 'tasks'
    id = Column(Integer, primary_key=True, index=True)
    theme_id = Column(Integer, ForeignKey('themes.id', ondelete='CASCADE'), nullable=False)
    name = Column(String(255), nullable=False)
    description = Column(String(255), nullable=True)
    status = Column(Enum(StatusEnum), default=StatusEnum.PLAN)
    theme = relationship('Theme', back_populates='tasks')

class EditTasks(BaseModel):
    name: Optional[str] = Field(None)
    description: Optional[str] = Field(None)
    status: Optional[str] = Field(None)

class ThemeRequest(BaseModel):
    name: str

class TaskRequest(BaseModel):
    name: str
    description: str
    status: Optional[str] = Field(StatusEnum.PLAN)




class TaskModel(BaseModel):
    id: int
    status: str
    description: str

class ThemeModel(BaseModel):
    id: int
    tasks: Dict[str, TaskModel]

class TodoListFormat(RootModel[Dict[str, ThemeModel]]):
    pass

Base.metadata.create_all(bind=create_engine(DATABASE_URL))