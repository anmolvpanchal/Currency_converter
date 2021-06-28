package com.example.currencyconverter.Main

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.API.ApiClient
import com.example.currencyconverter.API.ServiceGenerator
import com.example.currencyconverter.Adaptors.AdapterTotalSelectedCurrencies
import com.example.currencyconverter.Adaptors.CustomSpinnerAdapter
import com.example.currencyconverter.ModelClasses.CurrentCountries_API
import com.example.currencyconverter.ModelClasses.CurrentRate_API
import com.example.currencyconverter.ModelClasses.TotalSelectedCurrenciesList
import com.example.currencyconverter.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    val getCurrentRateList: ArrayList<CurrentRate_API> = ArrayList()
    val getCurrentCountries: ArrayList<CurrentCountries_API> = ArrayList()
    val getAddedCurrencyList: ArrayList<TotalSelectedCurrenciesList> = ArrayList()
    val currentQuoteList: ArrayList<Double> = ArrayList()
    val cadRate : ArrayList<Double> = ArrayList()
    lateinit var recyclerView: RecyclerView
    var spinner_item: String? = null
    var editTextInput: String? = null
    lateinit var totalAmmountToDisplay: TextView
    val usFormatter: NumberFormat = NumberFormat.getInstance(Locale("en", "US"))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.addedCountryList) as RecyclerView
        totalAmmountToDisplay = findViewById(R.id.totalBalanceValueText) as TextView
        val btn_popup = findViewById(R.id.floatingActionButton) as FloatingActionButton

        getCurrenciesList()
        getCAD("CAD")

        btn_popup.setOnClickListener {

            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.custom_dialogbox)
            dialog.setCancelable(true)

            // set the custom dialog components - text,  button

            val edittext = dialog.findViewById<View>(R.id.dialogbox_editText) as EditText
            val button = dialog.findViewById<View>(R.id.dialogbox_button) as Button
            val spinner = dialog.findViewById<View>(R.id.dialogbox_spinner) as Spinner
            spinner.adapter = CustomSpinnerAdapter(this, getCurrentCountries)
            spinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    // TODO Auto-generated method stub
                    spinner_item = getCurrentCountries[position].code
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // TODO Auto-generated method stub
                }
            }
            button.setOnClickListener { // TODO Auto-generated method stub

                editTextInput = edittext.text.toString()
                if (editTextInput.isNullOrEmpty()) {
                    Toast.makeText(
                        applicationContext,
                        "Please enter some ammount",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    dialog.dismiss()
                    var ammount = usFormatter.format(editTextInput!!.toDouble())
                    getAddedCurrencyList.add(
                        TotalSelectedCurrenciesList(
                            spinner_item.toString(),
                            ammount.toString()
                        )
                    )

                    recyclerView.visibility = View.VISIBLE
                    var addText = findViewById(R.id.addAmmountText) as TextView
                    addText.visibility = View.GONE
                    getTotalSelectedCurrencies()
                    getSingleCurrency(spinner_item.toString(),ammount)

                }
            }
            dialog.show()
        }
    }

    fun updateTotal() {
        var totalAmmount: Double = currentQuoteList.sum()
        var cadQuote = cadRate.get(0)

        var ammountInCAD = totalAmmount * cadQuote
        Log.e("Shared preference vlue" , ammountInCAD.toString())
        if (getAddedCurrencyList.isNullOrEmpty()){
            totalAmmountToDisplay.setText("0.00 CAD")
        }else{
            val toDisplayAmount = usFormatter.format(ammountInCAD)
            totalAmmountToDisplay.setText(toDisplayAmount.toString() + " CAD")
        }

    }

    fun getTotalSelectedCurrencies() {

        recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 1)
        recyclerView.adapter = AdapterTotalSelectedCurrencies(
            this,
            getAddedCurrencyList,
            getCurrentCountries,
            currentQuoteList
        )
        (recyclerView.adapter as AdapterTotalSelectedCurrencies).notifyDataSetChanged()


    }

    fun getCAD (countryCode: String) {
        val apiClient = ServiceGenerator.getClient().create(ApiClient::class.java)
        val call = apiClient.getSingleRate(countryCode)
        var quote: String? = null
        var cadTODouble : Double? = null

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (!response.isSuccessful()) {
                    Toast.makeText(
                        getApplicationContext(),
                        "Something went Wrong !!" + response.code(),
                        Toast.LENGTH_SHORT
                    ).show();
                    Log.e(
                        "!response.isSuccessful", "body \n"
                                + response.errorBody().toString()
                                + " code ${response.code()}"
                    )
                    return
                }


                val respoce = response.body()?.string()
                val rootObject = JSONObject(respoce)
                quote = rootObject.getString("quote")
                var cadTODouble = quote?.toDouble()
                if (cadTODouble != null) {
                    cadRate.add(cadTODouble)
                }
                Log.e("responce API CAll", cadTODouble.toString())

            }


            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                    getApplicationContext(),
                    "Please check your internet connection",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("error on faliur", t.toString())
                t.printStackTrace()
            }

        })


    }

    fun getSingleCurrency(countryCode: String, ammount: String) {
        var progressDialog: ProgressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading Content");
        progressDialog.setMessage("Please Wait !");
        progressDialog.setCancelable(false);
        progressDialog.show();

        val apiClient = ServiceGenerator.getClient().create(ApiClient::class.java)
        val call = apiClient.getSingleRate(countryCode)
        var quote: String? = null

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()

                if (!response.isSuccessful()) {
                    Toast.makeText(
                        getApplicationContext(),
                        "Something went Wrong !!" + response.code(),
                        Toast.LENGTH_SHORT
                    ).show();
                    Log.e(
                        "!response.isSuccessful", "body \n"
                                + response.errorBody().toString()
                                + " code ${response.code()}"
                    )
                    return
                }

                val respoce = response.body()?.string()
                val rootObject = JSONObject(respoce)
                quote = rootObject.getString("quote")
                Log.e("responce API CAll", quote)

                var trimed = ammount.replace(",","")
                var editextammount = trimed.toDouble()
                Log.e("responce API CAll", editextammount.toString())

                var quotetostring = quote.toString()
                var quotetodouble = quotetostring.toDouble()

                var ammountineuro =  editextammount / quotetodouble

                Log.e("ammount in euro", ammountineuro.toString())


                currentQuoteList.add(ammountineuro)

                val totalAmmount = currentQuoteList.sum()
                var cadQuote = cadRate.get(0)

                var ammountInCAD = totalAmmount * cadQuote
                val toDisplayAmount = usFormatter.format(ammountInCAD)
                totalAmmountToDisplay.setText(toDisplayAmount.toString() + " CAD")

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(
                    getApplicationContext(),
                    "Please check your internet connection",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("error on faliur", t.toString())
                t.printStackTrace()
            }

        })

    }

    fun getCurrenciesList() {
        val apiClient = ServiceGenerator.getClient().create(ApiClient::class.java)
        val call = apiClient.getAllCurrencies()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (!response.isSuccessful()) {
                    Toast.makeText(
                        getApplicationContext(),
                        "Something went Wrong !!" + response.code(),
                        Toast.LENGTH_SHORT
                    ).show();
                    Log.e(
                        "!response.isSuccessful", "body \n"
                                + response.errorBody().toString()
                                + " code ${response.code()}"
                    )
                    return;
                }

                getCurrentRateList.clear()

                val respoce = response.body()?.string()
                val rootarray = JSONArray(respoce)
                for (i in 0 until rootarray.length()) {
                    val innerobject: JSONObject = rootarray.getJSONObject(i)
                    val code = innerobject.getString("code")
                    val name = innerobject.getString("name")

                    getCurrentCountries.add(CurrentCountries_API(code, name))

                }


            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                    getApplicationContext(),
                    "Please check your internet connection",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("error on faliur", t.toString())
                t.printStackTrace()
            }

        })
    }


    fun getCurrentRate() {

        val apiClient = ServiceGenerator.getClient().create(ApiClient::class.java)
        val call = apiClient.getCurrentRate()

        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (!response.isSuccessful()) {
                    Toast.makeText(
                        getApplicationContext(),
                        "Something went Wrong !!" + response.code(),
                        Toast.LENGTH_SHORT
                    ).show();
                    Log.e(
                        "!response.isSuccessful", "body \n"
                                + response.errorBody().toString()
                                + " code ${response.code()}"
                    )
                    return;
                }

                getCurrentRateList.clear()

                val respoce = response.body()?.string()

                Log.e("responce", respoce)


            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                    getApplicationContext(),
                    "Please check your internet connection",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("error on faliur", t.toString())
                t.printStackTrace()
            }

        })

    }
}