package com.example.sqlite_01

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.sqlite_01.`object`.EmpModelClass
import com.example.sqlite_01.helper.MyAdapter
import com.example.sqlite_01.model.DatabaseHandler
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.update_dialog.*
import java.sql.Types.NULL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewRecord()
    }

    fun saveRecord(view: View){
        val name = u_name.text.toString()
        val email = u_email.text.toString()
        val address = u_address.text.toString()
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        if(name.trim()!="" && email.trim()!="" && address.trim()!=""){
            val status = databaseHandler.addEmployee(EmpModelClass(NULL,name, email, address))
            if(status > -1){
                Toast.makeText(applicationContext,"record save",Toast.LENGTH_LONG).show()
                u_name.text.clear()
                u_email.text.clear()
                u_address.text.clear()
                viewRecord()
            }
        }else{
            Toast.makeText(applicationContext,"name, email, dan address tidak diperbolehkan kosong",Toast.LENGTH_LONG).show()
        }

    }

    // fungsi untuk membaca data dari database dan menampilkannya dari listview
    fun viewRecord(){
        // membuat instanisasi databasehandler
        val databaseHandler: DatabaseHandler= DatabaseHandler(this)

        // memamnggil fungsi viewemployee dari databsehandler untuk mengambil data
        val emp: List<EmpModelClass> = databaseHandler.viewEmployee()
        val empArrayId = Array<String>(emp.size){"0"}
        val empArrayName = Array<String>(emp.size){"null"}
        val empArrayEmail = Array<String>(emp.size){"null"}
        val empArrayAddress = Array<String>(emp.size){"null"}
        var index = 0

        // setiap data yang didapatkan dari database akan dimasukkan ke array
        for(e in emp){
            empArrayId[index] = e.userId.toString()
            empArrayName[index] = e.userName
            empArrayEmail[index] = e.userEmail
            empArrayAddress[index] = e.userAddress
            index++
        }

        // membuat customadapter untuk view UI
        val myListAdapter = MyAdapter(this,empArrayId,empArrayName,empArrayEmail,empArrayAddress)
        listView.adapter = myListAdapter

        listView.setOnItemClickListener{
            parent, view, position, id ->

            var view_id = empArrayId[position]
            var view_name = empArrayName[position]
            var view_email = empArrayEmail[position]
            var view_address = empArrayAddress[position]

            openRecord(view_id, view_name, view_email, view_address)
        }
    }

    private fun openRecord(data1: String, data2: String, data3: String, data4: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.update_dialog, null)
        dialogBuilder.setView(dialogView)

        val editId = dialogView.findViewById(R.id.updateId) as EditText
        val editName = dialogView.findViewById(R.id.updateName) as EditText
        val editEmail = dialogView.findViewById(R.id.updateEmail) as EditText
        val editAddress = dialogView.findViewById(R.id.updateAddress) as EditText

        editId.setText(data1)
        editName.setText(data2)
        editEmail.setText(data3)
        editAddress.setText(data4)

        val updateId = editId.text.toString()
        val updateName = editName.text.toString()
        val updateEmail = editEmail.text.toString()
        val updateAddress = editAddress.text.toString()

        dialogBuilder.setTitle("Pembaruan data id $data1")
        dialogBuilder.setMessage("Isi data dibawha ini")
        dialogBuilder.setPositiveButton("Update", DialogInterface.OnClickListener { _, _ ->

            val databaseHandler: DatabaseHandler= DatabaseHandler(this)
            if(updateId.trim()!="" && updateName.trim()!="" && updateEmail.trim()!="" && updateAddress.trim()!=""){

                val status = databaseHandler.updateEmployee(EmpModelClass(Integer.parseInt(updateId), updateName, updateEmail, updateAddress))
                if(status > -1){
                    Toast.makeText(applicationContext,"data terupdate",Toast.LENGTH_LONG).show()
                    viewRecord()
                }
            }else{
                Toast.makeText(applicationContext,"id dan email tidak diperbolehkan kosong",Toast.LENGTH_LONG).show()
            }

        })

        dialogBuilder.setNegativeButton("Hapus", DialogInterface.OnClickListener { _, _ ->

            val databaseHandler: DatabaseHandler= DatabaseHandler(this)
            if(updateId.trim()!=""){

                val status = databaseHandler.deleteEmployee(EmpModelClass(Integer.parseInt(updateId),"","", ""))
                if(status > -1){
                    Toast.makeText(applicationContext,"data terhapus",Toast.LENGTH_LONG).show()
                    viewRecord()
                }
            }else{
                Toast.makeText(applicationContext,"id tidak boleh kosong",Toast.LENGTH_LONG).show()
            }
        })

        dialogBuilder.setNeutralButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            // tidak melakukan apa2 :)
        })
        val b = dialogBuilder.create()
        b.show()
    }


}
