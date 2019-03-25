package dev.jishin.android.weatherapp.home.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.jishin.android.weatherapp.R
import dev.jishin.android.weatherapp.network.models.ForecastDay
import kotlinx.android.synthetic.main.rv_item_forecast.view.*
import java.text.SimpleDateFormat
import java.util.*

class RvForecastAdapter :
    RecyclerView.Adapter<RvForecastAdapter.ForecastVH>() {

    private val forecasts: ArrayList<ForecastDay> = arrayListOf()

    fun updateForecastItems(items: List<ForecastDay>) {
        forecasts.clear()
        forecasts.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_forecast, parent, false)
        return ForecastVH(view)
    }

    override fun getItemCount() = forecasts.size

    override fun onBindViewHolder(holder: ForecastVH, position: Int) {
        holder.bind(forecasts[position])
    }

    inner class ForecastVH(view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        fun bind(forecastItem: ForecastDay) {
            with(itemView) {
                val dayDate = SimpleDateFormat("yyyy-mm-dd", Locale.getDefault()).parse(forecastItem.date)
                val day = SimpleDateFormat("EEEE", Locale.getDefault()).format(dayDate)
                tvDay.text = day
                tvTemp.text = "${Math.round(forecastItem.day?.averageTempC ?: 0f)} C"
            }
        }
    }
}