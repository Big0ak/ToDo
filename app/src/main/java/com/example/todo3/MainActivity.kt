package com.example.todo3

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog

class MainActivity : ComponentActivity() {

    private val tasks = mutableListOf<String>()  // Коллекция задач в памяти
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // RecyclerView для отображения списка задач
        val recyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = TaskAdapter(tasks) { task -> onTaskRemoved(task) }

        }

        // Инициализируем адаптер
        this@MainActivity.adapter = recyclerView.adapter as TaskAdapter

        // Кнопка для добавления новой задачи
        val addButton = Button(this).apply {
            text = "Добавить задачу"
            setOnClickListener { showAddTaskDialog() }
        }

        // Линейный контейнер, который будет содержать RecyclerView и кнопку
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 40, 0, 0)  // Добавляем отступ сверху (в пикселях, можно изменить значение)
            addView(recyclerView)
            addView(addButton)
        }

        // Устанавливаем layout для активности
        setContentView(layout)

        // Свайп для удаления задачи
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback())
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    // Функция для отображения диалога добавления новой задачи
    private fun showAddTaskDialog() {
        val input = EditText(this)
        android.app.AlertDialog.Builder(this) // Используем AlertDialog из SDK
            .setTitle("Новая задача")
            .setView(input)
            .setPositiveButton("Добавить") { _, _ ->
                val task = input.text.toString()
                if (task.isNotBlank()) {
                    tasks.add(task)
                    adapter.notifyItemInserted(tasks.size - 1)
                } else {
                    Toast.makeText(this, "Задача не может быть пустой", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    // Функция для удаления задачи
    private fun onTaskRemoved(task: String) {
        val position = tasks.indexOf(task)
        if (position != -1) {
            tasks.removeAt(position)
            adapter.notifyItemRemoved(position)
        }
    }

    // Адаптер для RecyclerView
    inner class TaskAdapter(private val tasks: MutableList<String>, private val onTaskRemoved: (String) -> Unit) :
        RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): TaskViewHolder {
            val taskView = TextView(this@MainActivity)
            taskView.setPadding(16, 16, 16, 16)
            taskView.textSize = 18f
            return TaskViewHolder(taskView)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            val task = tasks[position]
            (holder.itemView as TextView).text = task
            holder.itemView.setOnClickListener {
                onTaskRemoved(task)
            }
        }

        override fun getItemCount() = tasks.size

        inner class TaskViewHolder(itemView: TextView) : RecyclerView.ViewHolder(itemView)
    }

    // Свайп для удаления задачи
    inner class SwipeToDeleteCallback : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val task = tasks[position]
            tasks.removeAt(position)
            adapter.notifyItemRemoved(position)
        }
    }
}
