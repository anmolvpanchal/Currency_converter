package com.example.currencyconverter.Adaptors

import android.app.Dialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.Main.MainActivity
import com.example.currencyconverter.ModelClasses.CurrentCountries_API
import com.example.currencyconverter.ModelClasses.TotalSelectedCurrenciesList
import com.example.currencyconverter.R
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterTotalSelectedCurrencies(
    var mainActivity: MainActivity,
    var getAddedCurrencyList: ArrayList<TotalSelectedCurrenciesList>,
    var getCurrentCountries: ArrayList<CurrentCountries_API>,
    var currentQuoteList: ArrayList<Double>
) : RecyclerView.Adapter<AdapterTotalSelectedCurrencies.MyViewHolder>(){

    var editTextInput: String? = null

    class MyViewHolder(mView: View) :RecyclerView.ViewHolder(mView) {

        val imag = mView.findViewById(R.id.flags) as ImageView
        var ammountString = mView.findViewById(R.id.countryTextAndAmount) as TextView
        val buttonEdit  = mView.findViewById(R.id.btnEdit) as TextView
        val buttonReomve = mView.findViewById(R.id.btnRemove) as TextView
        val toggleLayout = mView.findViewById(R.id.weightDistribution) as LinearLayout



    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterTotalSelectedCurrencies.MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(mainActivity).inflate(
                R.layout.recyclerview_cell,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(

        holder: AdapterTotalSelectedCurrencies.MyViewHolder,
        position: Int
    ) {

        holder.toggleLayout.visibility = View.GONE
        val obj = getAddedCurrencyList.get(position)

        holder.ammountString.text = obj.enteredAmmount +" "+ obj.code

        holder.ammountString.setOnClickListener {
            if (holder.toggleLayout.visibility == View.GONE){
                holder.toggleLayout.visibility = View.VISIBLE
            }else{
                holder.toggleLayout.visibility = View.GONE
            }
        }

        val countryCode = obj.code.dropLast(1)
        Log.e("countrycode", countryCode)
        val url = "https://www.countryflags.io/"+countryCode+"/flat/64.png"
        try {
            Picasso.get()
                .load("$url")
                //.fit()
                .into(holder.imag)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("text", url)
        }

        fun editAmmount(position: Int){
            val dialog = Dialog(mainActivity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.custom_dialogbox)
            dialog.setCancelable(true)

            // set the custom dialog components - text,  button

            val edittext = dialog.findViewById<View>(R.id.dialogbox_editText) as EditText
            var shoewInput = obj.enteredAmmount.replace(",","")
            edittext.setText(shoewInput)
            val button = dialog.findViewById<View>(R.id.dialogbox_button) as Button
            val spinner = dialog.findViewById<View>(R.id.dialogbox_spinner) as Spinner
            spinner.visibility = View.GONE
            button.setOnClickListener {
                editTextInput = edittext.text.toString()
                if (editTextInput.isNullOrEmpty()){
                    Toast.makeText(
                        mainActivity,
                        "Please enter some amount",
                        Toast.LENGTH_LONG
                    ).show()
                }else{
                    dialog.dismiss()
                    val usFormatter: NumberFormat = NumberFormat.getInstance(Locale("en", "US"))
                    var ammount = usFormatter.format(editTextInput!!.toDouble())
                    getAddedCurrencyList.set(position, TotalSelectedCurrenciesList(obj.code,ammount.toString()))
                    mainActivity.getSingleCurrency(obj.code,ammount.toString())
                    currentQuoteList.removeAt(position)
                    notifyDataSetChanged()
                    notifyItemChanged(position)
                    notifyItemRemoved(position)
                }
            }
            dialog.show()
        }
        holder.buttonEdit.setOnClickListener {
            editAmmount(position)
        }

        fun removeItem(position: Int){
            if (getAddedCurrencyList.isNullOrEmpty()){
                mainActivity.updateTotal()
            }else{
                getAddedCurrencyList.removeAt(position)
                currentQuoteList.removeAt(position)
                notifyItemRemoved(position)
                notifyDataSetChanged()
                notifyItemChanged(position)


            }
        }
        holder.buttonReomve.setOnClickListener {
                removeItem(position)
            mainActivity.updateTotal()
        }

    }


    override fun getItemCount(): Int {
        return getAddedCurrencyList.size
    }
}