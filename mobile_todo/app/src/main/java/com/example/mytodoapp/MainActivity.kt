package com.example.mytodoapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var layoutThemes: LinearLayout
    private val themesMap = mutableMapOf<String, MutableMap<String, JSONObject>>()
    private val REQUEST_CODE_CREATE_FILE = 1
    private val REQUEST_CODE_OPEN_FILE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация элементов интерфейса
        val buttonAddTheme = findViewById<Button>(R.id.button_add_theme)
        val buttonOpenJson = findViewById<Button>(R.id.button_open_json)
        val buttonChooseSavePath = findViewById<Button>(R.id.button_choose_save_path)
        val layoutInput = findViewById<LinearLayout>(R.id.layout_input)
        val editTextThemeName = findViewById<EditText>(R.id.edit_text_theme_name)
        val buttonCreate = findViewById<Button>(R.id.button_create)
        layoutThemes = findViewById(R.id.layout_themes)

        // При нажатии на кнопку отображаем блок с полем ввода и кнопкой "Создать"
        buttonAddTheme.setOnClickListener {
            layoutInput.visibility = View.VISIBLE
        }

        // При нажатии на кнопку "Создать" добавляем блок с введённым текстом и кнопками "Изменить" и "Удалить"
        buttonCreate.setOnClickListener {
            val themeName = editTextThemeName.text.toString()
            if (themeName.isNotEmpty()) {
                // Создаем новый вертикальный LinearLayout для темы
                val themeLayout = LinearLayout(this)
                themeLayout.orientation = LinearLayout.VERTICAL

                // Добавляем границу для темы
                val themeBackground = GradientDrawable()
                themeBackground.setStroke(5, Color.BLACK) // Толщина и цвет границы
                themeBackground.cornerRadius = 16f // Радиус закругления углов
                themeLayout.background = themeBackground

                // Устанавливаем отступы между темами
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(16, 16, 16, 16)
                themeLayout.layoutParams = layoutParams

                // Горизонтальный Layout для названия темы и кнопок "Изменить" и "Удалить"
                val themeHeaderLayout = LinearLayout(this)
                themeHeaderLayout.orientation = LinearLayout.HORIZONTAL
                themeHeaderLayout.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                // TextView для названия темы
                val textView = TextView(this)
                textView.text = themeName
                textView.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )

                // Кнопка "Изменить"
                val buttonEdit = ImageButton(this)
                buttonEdit.setImageResource(android.R.drawable.ic_menu_edit)
                buttonEdit.setOnClickListener {
                    editTextThemeName.setText(textView.text)
                    layoutInput.visibility = View.VISIBLE

                    // При сохранении заменяем текст в TextView
                    buttonCreate.setOnClickListener {
                        val updatedThemeName = editTextThemeName.text.toString()
                        if (updatedThemeName.isNotEmpty()) {
                            val oldName = textView.text.toString()
                            textView.text = updatedThemeName
                            themesMap[updatedThemeName] = themesMap.remove(oldName) ?: mutableMapOf()
                            layoutInput.visibility = View.GONE
                        }
                    }
                }

                // Кнопка "Удалить"
                val buttonDelete = ImageButton(this)
                buttonDelete.setImageResource(android.R.drawable.ic_menu_delete)
                buttonDelete.setOnClickListener {
                    // Удаление темы
                    layoutThemes.removeView(themeLayout)
                    themesMap.remove(textView.text.toString())
                }

                // Добавляем элементы в горизонтальный layout
                themeHeaderLayout.addView(textView)
                themeHeaderLayout.addView(buttonEdit)
                themeHeaderLayout.addView(buttonDelete)

                // Контейнер для задач
                val tasksLayout = LinearLayout(this)
                tasksLayout.orientation = LinearLayout.VERTICAL

                // Кнопка для добавления задачи
                val buttonAddTask = Button(this)
                buttonAddTask.text = "Добавить задачу"
                buttonAddTask.setOnClickListener {
                    val editTextTask = EditText(this)
                    editTextTask.hint = "Введите задачу"

                    val buttonSaveTask = Button(this)
                    buttonSaveTask.text = "Сохранить"
                    buttonSaveTask.setOnClickListener {
                        val taskText = editTextTask.text.toString()
                        if (taskText.isNotEmpty()) {
                            // Создаем горизонтальный Layout для задачи
                            val taskLayout = LinearLayout(this)
                            taskLayout.orientation = LinearLayout.HORIZONTAL

                            // TextView для задачи
                            val taskView = TextView(this)
                            taskView.text = taskText
                            taskView.layoutParams = LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1f
                            )

                            // TextView для статуса задачи
                            val taskStatus = TextView(this)
                            var currentStatus = "Планируется"  // Статус по умолчанию
                            taskStatus.text = currentStatus
                            taskStatus.setPadding(16, 0, 16, 0)

                            // Кнопка для переключения статуса задачи
                            val buttonChangeStatus = ImageButton(this)
                            buttonChangeStatus.setImageResource(android.R.drawable.ic_menu_sort_by_size) // Замените на иконку для статуса
                            buttonChangeStatus.setOnClickListener {
                                currentStatus = when (currentStatus) {
                                    "Планируется" -> "Выполняется"
                                    "Выполняется" -> "Готово"
                                    else -> "Планируется"
                                }
                                taskStatus.text = currentStatus
                            }

                            // Кнопка "Изменить задачу"
                            val buttonEditTask = ImageButton(this)
                            buttonEditTask.setImageResource(android.R.drawable.ic_menu_edit)
                            buttonEditTask.setOnClickListener {
                                editTextTask.setText(taskView.text)
                                tasksLayout.addView(editTextTask, tasksLayout.indexOfChild(taskLayout) + 1)
                                tasksLayout.addView(buttonSaveTask, tasksLayout.indexOfChild(taskLayout) + 2)
                                tasksLayout.removeView(taskLayout)
                            }

                            // Кнопка "Удалить задачу"
                            val buttonDeleteTask = ImageButton(this)
                            buttonDeleteTask.setImageResource(android.R.drawable.ic_menu_delete)
                            buttonDeleteTask.setOnClickListener {
                                // Удаление задачи
                                tasksLayout.removeView(taskLayout)
                                themesMap[textView.text.toString()]?.remove(taskView.text.toString())
                            }

                            // Добавляем элементы в горизонтальный layout задачи
                            taskLayout.addView(taskView)
                            taskLayout.addView(taskStatus)
                            taskLayout.addView(buttonChangeStatus)
                            taskLayout.addView(buttonEditTask)
                            taskLayout.addView(buttonDeleteTask)

                            // Добавляем задачу в контейнер задач
                            tasksLayout.addView(taskLayout)

                            // Сохраняем задачу в словаре
                            themesMap[textView.text.toString()]?.put(taskText, JSONObject().apply {
                                put("status", currentStatus)
                                put("description", "")
                            })

                            // Убираем поле ввода и кнопку сохранения
                            tasksLayout.removeView(editTextTask)
                            tasksLayout.removeView(buttonSaveTask)
                        }
                    }

                    tasksLayout.addView(editTextTask)
                    tasksLayout.addView(buttonSaveTask)
                }

                // Добавляем заголовок темы, задачи и кнопку добавления задачи в основной блок темы
                themeLayout.addView(themeHeaderLayout)
                themeLayout.addView(tasksLayout)
                themeLayout.addView(buttonAddTask)

                // Добавляем тему в основной layout
                layoutThemes.addView(themeLayout)
                themesMap[themeName] = mutableMapOf()

                // Очищаем поле ввода и скрываем блок
                editTextThemeName.text.clear()
                layoutInput.visibility = View.GONE
            }
        }

        // При нажатии на кнопку "Открыть JSON" открываем файл
        buttonOpenJson.setOnClickListener {
            openJsonFile()
        }

        // При нажатии на кнопку "Выбрать путь для сохранения" выбираем путь
        buttonChooseSavePath.setOnClickListener {
            createFile()
        }
    }

    private fun saveJsonToUri(uri: Uri) {
        try {
            val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
            outputStream?.use {
                val jsonObject = JSONObject()
                themesMap.forEach { (themeName, tasks) ->
                    val themeJson = JSONObject()
                    tasks.forEach { (taskName, taskDetails) ->
                        themeJson.put(taskName, taskDetails)
                    }
                    jsonObject.put(themeName, themeJson)
                }
                it.write(jsonObject.toString().toByteArray())
                it.flush()
                Toast.makeText(this, "JSON сохранен в $uri", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка при сохранении JSON: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openJsonFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
        startActivityForResult(intent, REQUEST_CODE_OPEN_FILE)
    }

    private fun loadJsonFromUri(uri: Uri) {
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val jsonObject = JSONObject(inputStream.bufferedReader().use { it.readText() })
                layoutThemes.removeAllViews()
                themesMap.clear()

                jsonObject.keys().forEach { themeName ->
                    val themeJson = jsonObject.getJSONObject(themeName)

                    // Создаем новый вертикальный LinearLayout для темы
                    val themeLayout = LinearLayout(this)
                    themeLayout.orientation = LinearLayout.VERTICAL

                    // Добавляем границу для темы
                    val themeBackground = GradientDrawable()
                    themeBackground.setStroke(5, Color.BLACK)
                    themeBackground.cornerRadius = 16f
                    themeLayout.background = themeBackground

                    // Устанавливаем отступы между темами
                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.setMargins(16, 16, 16, 16)
                    themeLayout.layoutParams = layoutParams

                    // Горизонтальный Layout для названия темы и кнопок "Изменить" и "Удалить"
                    val themeHeaderLayout = LinearLayout(this)
                    themeHeaderLayout.orientation = LinearLayout.HORIZONTAL
                    themeHeaderLayout.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    // TextView для названия темы
                    val textView = TextView(this)
                    textView.text = themeName
                    textView.layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )

                    // Кнопка "Изменить"
                    val buttonEdit = ImageButton(this)
                    buttonEdit.setImageResource(android.R.drawable.ic_menu_edit)
                    buttonEdit.setOnClickListener {
                        // Действия при нажатии на кнопку "Изменить"
                    }

                    // Кнопка "Удалить"
                    val buttonDelete = ImageButton(this)
                    buttonDelete.setImageResource(android.R.drawable.ic_menu_delete)
                    buttonDelete.setOnClickListener {
                        // Удаление темы
                        layoutThemes.removeView(themeLayout)
                        themesMap.remove(textView.text.toString())
                    }

                    // Добавляем элементы в горизонтальный layout
                    themeHeaderLayout.addView(textView)
                    themeHeaderLayout.addView(buttonEdit)
                    themeHeaderLayout.addView(buttonDelete)

                    // Контейнер для задач
                    val tasksLayout = LinearLayout(this)
                    tasksLayout.orientation = LinearLayout.VERTICAL

                    themeJson.keys().forEach { taskName ->
                        val taskDetails = themeJson.getJSONObject(taskName)

                        // Создаем горизонтальный Layout для задачи
                        val taskLayout = LinearLayout(this)
                        taskLayout.orientation = LinearLayout.HORIZONTAL

                        // TextView для задачи
                        val taskView = TextView(this)
                        taskView.text = taskName
                        taskView.layoutParams = LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        )

                        // TextView для статуса задачи
                        val taskStatus = TextView(this)
                        val currentStatus = taskDetails.getString("status")
                        taskStatus.text = currentStatus
                        taskStatus.setPadding(16, 0, 16, 0)

                        // Кнопка для переключения статуса задачи
                        val buttonChangeStatus = ImageButton(this)
                        buttonChangeStatus.setImageResource(android.R.drawable.ic_menu_sort_by_size)
                        buttonChangeStatus.setOnClickListener {
                            val newStatus = when (taskStatus.text.toString()) {
                                "Планируется" -> "Выполняется"
                                "Выполняется" -> "Готово"
                                else -> "Планируется"
                            }
                            taskStatus.text = newStatus
                            themesMap[themeName]?.put(taskView.text.toString(), taskDetails.put("status", newStatus))
                        }

                        // Кнопка "Изменить задачу"
                        val buttonEditTask = ImageButton(this)
                        buttonEditTask.setImageResource(android.R.drawable.ic_menu_edit)
                        buttonEditTask.setOnClickListener {
                            // Действия при нажатии на кнопку "Изменить задачу"
                        }

                        // Кнопка "Удалить задачу"
                        val buttonDeleteTask = ImageButton(this)
                        buttonDeleteTask.setImageResource(android.R.drawable.ic_menu_delete)
                        buttonDeleteTask.setOnClickListener {
                            tasksLayout.removeView(taskLayout)
                            themesMap[themeName]?.remove(taskView.text.toString())
                        }

                        // Добавляем элементы в горизонтальный layout задачи
                        taskLayout.addView(taskView)
                        taskLayout.addView(taskStatus)
                        taskLayout.addView(buttonChangeStatus)
                        taskLayout.addView(buttonEditTask)
                        taskLayout.addView(buttonDeleteTask)

                        // Добавляем задачу в контейнер задач
                        tasksLayout.addView(taskLayout)
                    }

                    // Добавляем заголовок темы, задачи и кнопку добавления задачи в основной блок темы
                    themeLayout.addView(themeHeaderLayout)
                    themeLayout.addView(tasksLayout)

                    // Добавляем тему в основной layout
                    layoutThemes.addView(themeLayout)
                    themesMap[themeName] = mutableMapOf()
                }

                Toast.makeText(this, "JSON загружен успешно", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка при загрузке JSON: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, "data.json")
        }
        startActivityForResult(intent, REQUEST_CODE_CREATE_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                when (requestCode) {
                    REQUEST_CODE_CREATE_FILE -> saveJsonToUri(uri)
                    REQUEST_CODE_OPEN_FILE -> loadJsonFromUri(uri)
                }
            }
        }
    }
}
