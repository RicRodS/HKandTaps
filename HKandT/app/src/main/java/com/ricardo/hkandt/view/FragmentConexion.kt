package com.ricardo.hkandt.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.ricardo.hkandt.databinding.FragmentConexionBinding
import com.ricardo.hkandt.model.ListaAcciones
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.Socket
import java.util.Scanner

class FragmentConexion : Fragment() {

    private lateinit var binding: FragmentConexionBinding
    private lateinit var navController: NavController
    private lateinit var ip: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConexionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = findNavController()
        ip = "IP"
        binding.textView.text = "Introduce la dirección IP a la que conectarte:"
        binding.btnConectar.setOnClickListener {
            onClickBtnConectar()
        }
        binding.btnAyudaIP.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://support.microsoft.com/es-es/windows/busque-su-dirección-ip-en-windows-f21a9bbc-c582-55cd-35e0-73431160a1b9"))
            startActivity(intent)
        }
        binding.textView.doOnTextChanged { text, start, before, count ->
            val accion = FragmentConexionDirections.actionFragmentConexionToFragmentListasAcciones(ip)
            navController.navigate(accion)
        }
    }

    fun onClickBtnConectar(){
        val posibleIP = binding.textInputLayoutIP.editText!!.text.toString()
        if (posibleIP.isEmpty()) {
            Toast.makeText(context, "La IP está vacía", Toast.LENGTH_SHORT).show()
        }else if (esUnaIP(posibleIP)){
            binding.progressBar.visibility = View.VISIBLE
            CoroutineScope(IO).launch {
                try {
                    val socket = Socket()
                    socket.connect(InetSocketAddress(posibleIP, 11223), 5000)
                    val outputStream = socket.getOutputStream()
                    var envio = String("L".toByteArray(), Charsets.US_ASCII).toByteArray()
                    outputStream.write(envio)
                    val input = Scanner(socket.getInputStream())
                    var listaMovilJson = input.nextLine()
                    socket.close()
                    val listaAcciones = Gson().fromJson(listaMovilJson, ListaAcciones::class.java)
                    ip = posibleIP
                    binding.textView.text = "Abriendo " + listaAcciones.nombre + "..."
                    binding.progressBar.visibility = View.INVISIBLE
                }catch (e:Exception){
                    binding.progressBar.visibility = View.INVISIBLE
                }
            }
        }else{
            Toast.makeText(context, "No has introducido una IP correcta" + posibleIP, Toast.LENGTH_SHORT).show()
        }
    }

    fun esUnaIP(ip:String):Boolean{
        val parts = ip.split(".");
        if (parts.size != 4) {
            return false
        }
        for (part:String in parts) {
            try {
                val num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false
                }
            } catch (e:NumberFormatException) {
                return false
            }
        }
        return true;
    }
}