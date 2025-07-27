package com.info.smsforwarder.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.info.smsforwarder.databinding.ActivityMainBinding
import com.info.smsforwarder.databinding.ListItemSmsBinding

data class SmsItem(
    val name: String,
    val message: String,
    val mobile: String,
    var status: String = "Not sent"
)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val items = listOf(
        SmsItem("Alice", "Hello Alice!", "+1 234 567 890"),
        SmsItem("Bob", "Meeting at 5pm", "+1 987 654 321"),
        SmsItem("Charlie", "Check this out", "+1 555 123 456"),
        SmsItem("Alice", "Hello Alice!", "+1 234 567 890"),
        SmsItem("Bob", "Meeting at 5pm", "+1 987 654 321"),
        SmsItem("Charlie", "Check this out", "+1 555 123 456")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val adapter = SmsAdapter(items)
        binding.listView.adapter = adapter
    }

    inner class SmsAdapter(private val data: List<SmsItem>) : BaseAdapter() {
        override fun getCount() = data.size
        override fun getItem(position: Int) = data[position]
        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val holder: ViewHolder
            val view: View

            if (convertView == null) {
                val itemBinding = ListItemSmsBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
                view = itemBinding.root
                holder = ViewHolder(itemBinding)
                view.tag = holder
            } else {
                view = convertView
                holder = view.tag as ViewHolder
            }

            val item = getItem(position)
            holder.bind(item)

            return view
        }

        inner class ViewHolder(private val itemBinding: ListItemSmsBinding) {
            fun bind(item: SmsItem) {
                itemBinding.nameText.text = item.name
                itemBinding.messageText.text = item.message
                itemBinding.numberText.text = item.mobile
                itemBinding.statusText.text = item.status

                itemBinding.statusText.setTextColor(
                    when (item.status) {
                        "Delivered" -> Color.parseColor("#008000")
                        "Failed" -> Color.RED
                        else -> Color.GRAY
                    }
                )

                itemBinding.sendBtn.setOnClickListener {
                    val progressDialog = androidx.appcompat.app.AlertDialog.Builder(this@MainActivity)
                        .setTitle("Sending…")
                        .setMessage("Sending '${item.message}' to ${item.mobile}")
                        .setCancelable(false)
                        .show()

                    itemBinding.root.postDelayed({
                        progressDialog.dismiss()

                        val delivered = listOf("Delivered", "Failed").random()
                        item.status = delivered

                        itemBinding.statusText.text = delivered
                        itemBinding.statusText.setTextColor(
                            if (delivered == "Delivered") Color.parseColor("#008000")
                            else Color.RED
                        )

                        androidx.appcompat.app.AlertDialog.Builder(this@MainActivity)
                            .setTitle("Result")
                            .setMessage("Message to ${item.name} (${item.mobile}) $delivered!")
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .show()
                    }, 1500)
                }
            }
        }
    }
}
