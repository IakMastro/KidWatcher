package com.example.parentsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CallAdapter(val list: ArrayList<Call>) : RecyclerView.Adapter<CallAdapter.ViewHolder>() {
    public class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var statusImageView: ImageView
        private lateinit var numberTextView: TextView
        private lateinit var dateTextView: TextView
        private lateinit var durationTextView: TextView

        init {
            setReferences(itemView)
        }

        private fun setReferences(itemView: View) {
            statusImageView = itemView.findViewById(R.id.call_status)
            numberTextView = itemView.findViewById(R.id.call_number)
            dateTextView = itemView.findViewById(R.id.call_date)
            durationTextView = itemView.findViewById(R.id.duration)
        }

        public fun bind(call: Call) {
            numberTextView.text = call.number
            dateTextView.text = call.date
            durationTextView.text = call.duration
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.call_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
