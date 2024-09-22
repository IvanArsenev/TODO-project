let jsonData = {};
let currentTask = { theme: '', name: '' };
let addingNewTheme = false;
let currentTheme = '';
let draggedElement = null;
let currentThemeForEdit = '';

// Открытие модального окна для редактирования названия темы
function editTheme(theme) {
    currentThemeForEdit = theme;
    document.getElementById('newThemeName').value = theme;
    document.getElementById('editThemeModal').style.display = 'block';
}

// Сохранение изменений в названии темы
function saveTheme() {
    const newThemeName = document.getElementById('newThemeName').value;
    if (newThemeName && !jsonData[newThemeName] && newThemeName !== currentThemeForEdit) {
        jsonData[newThemeName] = jsonData[currentThemeForEdit];
        delete jsonData[currentThemeForEdit];
        renderBoard(jsonData);
        document.getElementById('editThemeModal').style.display = 'none';
    } else {
        alert('Такое название уже существует или название не заполнено');
    }
}

// Загрузка JSON файла (сделать проверку корректности файла)
document.getElementById('fileInput').addEventListener('change', function(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            try {
                jsonData = JSON.parse(e.target.result);
                renderBoard(jsonData);
            } catch (err) {
                alert('JSON инвалид (нужен другой JSON)');
            }
        };
        reader.readAsText(file);
    }
});

function renderBoard(data) {
    const board = document.getElementById('board');
    board.innerHTML = ''; // Очистка доски (мы же загружаем новый файл [если не сохранил, то печально, что сказать...])

    Object.entries(data).forEach(([theme, tasks]) => {
        const column = document.createElement('div');
        column.className = 'column';
        column.setAttribute('draggable', 'true');
        column.dataset.theme = theme;

        const columnTitle = document.createElement('h2');
        
        // Контейнер для заголовка и кнопки
        const titleContainer = document.createElement('div');
        titleContainer.style.display = 'flex';
        titleContainer.style.justifyContent = 'space-between';
        titleContainer.style.alignItems = 'center';

        // Заголовок темы
        const titleText = document.createElement('span');
        titleText.textContent = theme;
        titleContainer.appendChild(titleText);

        // Кнопка изменения названия темы
        const editButton = document.createElement('button');
        editButton.textContent = '✏️';
        editButton.onclick = () => editTheme(theme);
        titleContainer.appendChild(editButton);

        columnTitle.appendChild(titleContainer);
        column.appendChild(columnTitle);

        Object.entries(tasks).forEach(([taskName, taskDetails]) => {
            const taskElement = document.createElement('div');
            taskElement.className = 'task';
            taskElement.textContent = `${taskName} - ${taskDetails.status}`;
            taskElement.setAttribute('draggable', 'true');
            taskElement.dataset.taskName = taskName;
            taskElement.dataset.theme = theme;

            // Добавление обработчика клика для редактирования задачи (drag&drop все сломал, починить)
            taskElement.addEventListener('click', () => editTask(theme, taskName));

            column.appendChild(taskElement);
        });

        const addTaskButton = document.createElement('button');
        addTaskButton.className = 'add-task';
        addTaskButton.textContent = 'Добавить задачу';
        addTaskButton.onclick = () => addTask(theme);
        column.appendChild(addTaskButton);

        board.appendChild(column);
    });

    addDragAndDropListeners();
}



// Добавление слушателей событий для Drag & Drop
function addDragAndDropListeners() {
    const columns = document.querySelectorAll('.column');
    const tasks = document.querySelectorAll('.task');

    columns.forEach(column => {
        column.addEventListener('dragstart', handleDragStart);
        column.addEventListener('dragover', handleDragOver);
        column.addEventListener('drop', handleDrop);
        column.addEventListener('dragend', handleDragEnd);
    });

    tasks.forEach(task => {
        task.addEventListener('dragstart', handleDragStart);
        task.addEventListener('dragover', handleDragOver);
        task.addEventListener('drop', handleDrop);
        task.addEventListener('dragend', handleDragEnd);
    });
}

// Начало перетаскивания
function handleDragStart(e) {
    draggedElement = e.target;
    e.dataTransfer.effectAllowed = 'move';
    setTimeout(() => {
        draggedElement.classList.add('dragging');
    }, 0);
}

// Обработка события перетаскивания над элементом (почему так криво работает???)
function handleDragOver(e) {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
    const target = e.target;

    if (draggedElement.classList.contains('task') && target.classList.contains('task')) {
        target.parentElement.insertBefore(draggedElement, target.nextSibling);
    } else if (draggedElement.classList.contains('column') && target.classList.contains('column')) {
        const board = document.getElementById('board');
        board.insertBefore(draggedElement, target.nextSibling);
    } else if (target.classList.contains('column') && draggedElement.classList.contains('task')) {
        target.appendChild(draggedElement);
    }
    // else что?
}

// Обработка события сброса элемента
function handleDrop(e) {
    e.stopPropagation();

    if (draggedElement.classList.contains('task')) {
        const targetTheme = e.target.closest('.column').dataset.theme;
        const taskName = draggedElement.dataset.taskName;
        const sourceTheme = draggedElement.dataset.theme;

        if (targetTheme && sourceTheme !== targetTheme) {
            const taskData = jsonData[sourceTheme][taskName];
            delete jsonData[sourceTheme][taskName];

            jsonData[targetTheme][taskName] = taskData;
        } else if (sourceTheme === targetTheme) {
            const reorderedTasks = {};
            e.target.closest('.column').querySelectorAll('.task').forEach(task => {
                const name = task.dataset.taskName;
                reorderedTasks[name] = jsonData[targetTheme][name];
            });

            jsonData[targetTheme] = reorderedTasks;
        }

        renderBoard(jsonData);
    } else if (draggedElement.classList.contains('column')) {
        const reorderedThemes = {};
        document.querySelectorAll('.column').forEach(column => {
            const themeName = column.dataset.theme;
            reorderedThemes[themeName] = jsonData[themeName];
        });

        jsonData = reorderedThemes;
        renderBoard(jsonData);
    }

    return false;
}

// Завершение перетаскивания
function handleDragEnd() {
    this.classList.remove('dragging');
    draggedElement = null;
}

// Открытие модального окна для редактирования задачи
function editTask(theme, taskName) {
    currentTask = { theme, name: taskName };

    const task = jsonData[theme][taskName];

    document.getElementById('taskName').value = taskName;
    document.getElementById('taskStatus').value = task.status;
    document.getElementById('taskDescription').value = task.description;

    document.getElementById('taskModal').style.display = 'block';
}

// Закрытие модального окна
document.querySelectorAll('.close').forEach(element => {
    element.onclick = function() {
        element.parentElement.parentElement.style.display = 'none';
    };
});

// Сохранение изменений в задаче
function saveTask() {
    const newTaskName = document.getElementById('taskName').value;
    const newStatus = document.getElementById('taskStatus').value;
    const newDescription = document.getElementById('taskDescription').value;

    const task = jsonData[currentTask.theme][currentTask.name];

    task.status = newStatus;
    task.description = newDescription;

    if (newTaskName !== currentTask.name) {
        delete jsonData[currentTask.theme][currentTask.name];
        jsonData[currentTask.theme][newTaskName] = task;
        currentTask.name = newTaskName;
    }

    document.getElementById('taskModal').style.display = 'none';
    renderBoard(jsonData);
}

// Открытие модального окна для добавления новой темы
function addTheme() {
    addingNewTheme = true;
    document.getElementById('newItemTitle').textContent = 'Создать тему';
    document.getElementById('newItemName').value = '';
    document.getElementById('newItemDescription').style.display = 'none'; // можно разделить модальное окно создания на 2, чтобы избавиться от описания, но оно разве мешает?
    document.getElementById('newItemModal').style.display = 'block';
}

// Открытие модального окна для добавления новой задачи
function addTask(theme) {
    addingNewTheme = false;
    currentTheme = theme;
    document.getElementById('newItemTitle').textContent = 'Добавить задачу';
    document.getElementById('newItemName').value = '';
    document.getElementById('newItemDescription').value = '';
    document.getElementById('newItemDescription').style.display = 'block';
    document.getElementById('newItemModal').style.display = 'block';
}

// Сохранение новой задачи или темы
function saveNewItem() {
    const newItemName = document.getElementById('newItemName').value;
    const newItemDescription = document.getElementById('newItemDescription').value;

    if (addingNewTheme) {
        if (newItemName && !jsonData[newItemName]) {
            jsonData[newItemName] = {};
            renderBoard(jsonData);
        } else {
            alert('Такая тема уже есть или название не заполнено');
        }
    } else {
        if (newItemName && !jsonData[currentTheme][newItemName]) {
            jsonData[currentTheme][newItemName] = {
                status: 'Планируется',
                description: newItemDescription
            };
            renderBoard(jsonData);
        } else {
            alert('Такая задача уже есть или название не заполнено');
        }
    }

    document.getElementById('newItemModal').style.display = 'none';
}

// Сохранение JSON файла
document.getElementById('saveJsonBtn').addEventListener('click', function() {
    const jsonStr = JSON.stringify(jsonData, null, 2);
    const blob = new Blob([jsonStr], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'tasks.json';
    a.click();
    URL.revokeObjectURL(url);
});
