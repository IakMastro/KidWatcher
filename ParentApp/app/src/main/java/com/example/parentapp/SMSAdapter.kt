package com.example.parentapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SMSAdapter(val smsList: ArrayList<SMS>): RecyclerView.Adapter<SMSAdapter.ViewHolder>() {
    public class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var statusImageView: ImageView
        private lateinit var numberTextView: TextView
        private lateinit var dateTextView: TextView
        private lateinit var messageTextView: TextView

        init {
            setReferences(itemView)
        }

        private fun setReferences(itemView: View) {
            statusImageView = itemView.findViewById(R.id.sms_status)
            numberTextView = itemView.findViewById(R.id.sms_number)
            dateTextView = itemView.findViewById(R.id.sms_date)
            messageTextView = itemView.findViewById(R.id.message)
        }

        public fun bind(sms: SMS) {
            numberTextView.text = sms.number
            dateTextView.text = sms.date
            messageTextView.text = sms.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.sms_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(smsList[position])
    }

    override fun getItemCount(): Int {
        return smsList.size
    }
}