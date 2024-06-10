package com.ricardo.hkandt.view

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.ricardo.hkandt.databinding.FragmentListaAccionesBinding
import com.ricardo.hkandt.model.Accion
import com.ricardo.hkandt.model.ListaAcciones
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.Socket
import java.util.Scanner

class FragmentListasAcciones : Fragment() {

    private lateinit var binding: FragmentListaAccionesBinding
    private lateinit var navController: NavController
    private lateinit var ip: String
    private lateinit var acciones: List<Accion>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ip = it.get("ip") as String
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListaAccionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()
        val listaAccionesJson = "{'nombre':'ListaAcciones','acciones':[{'id':5,'nombre':'Acciones'}]}"
        val listaAcciones = Gson().fromJson(listaAccionesJson, ListaAcciones::class.java)
        acciones = listaAcciones.acciones
        with (binding.recyclerView){
            adapter = AccionAdapter(acciones){accion -> onClickAccion(accion)}
            layoutManager = GridLayoutManager(context, 3)
        }
        binding.btnLista1.setOnClickListener { onClickBtnLista(0) }
        binding.btnLista2.setOnClickListener { onClickBtnLista(1) }
        binding.btnLista3.setOnClickListener { onClickBtnLista(2) }
        binding.btnLista4.setOnClickListener { onClickBtnLista(3) }
        binding.btnLista5.setOnClickListener { onClickBtnLista(4) }
        binding.btnRecargar.setOnClickListener { obtenerListaEnUso() }
        binding.btnSalir.setOnClickListener { salirLista() }
        obtenerListaEnUso()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            binding.recyclerView.layoutManager = GridLayoutManager(context, 3)
        }else{
            binding.recyclerView.layoutManager = GridLayoutManager(context, 6)
        }
    }

    fun obtenerListaEnUso(){
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val socket = Socket()
                    socket.connect(InetSocketAddress(ip, 11223), 5000)
                    val outputStream = socket.getOutputStream()
                    var envio = String("L".toByteArray(), Charsets.US_ASCII).toByteArray()
                    outputStream.write(envio)
                    val input = Scanner(socket.getInputStream())
                    var listaMovilJson = input.nextLine()
                    socket.close()
                    val listaAcciones = Gson().fromJson(listaMovilJson, ListaAcciones::class.java)
                    acciones = listaAcciones.acciones
                    binding.progressBar.visibility = View.INVISIBLE
                }catch (e: Exception){
                    binding.progressBar.visibility = View.INVISIBLE
                }
            }
            binding.recyclerView.adapter = AccionAdapter(acciones){ accion -> onClickAccion(accion)}
        }
    }

    fun onClickAccion(accion: Accion){
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val socket = Socket()
                    socket.connect(InetSocketAddress(ip, 11223), 5000)
                    val outputStream = socket.getOutputStream()
                    val envio = String(accion.id.toString().toByteArray(), Charsets.US_ASCII).toByteArray()
                    outputStream.write(envio)
                    val input = Scanner(socket.getInputStream())
                    val resultado = input.nextLine()
                    if(resultado == "L"){
                        val listaMovilJson = input.nextLine()
                        val listaAcciones = Gson().fromJson(listaMovilJson, ListaAcciones::class.java)
                        acciones = listaAcciones.acciones
                    }
                    socket.close()
                    binding.progressBar.visibility = View.INVISIBLE
                }catch (e: Exception){
                    binding.progressBar.visibility = View.INVISIBLE
                }
            }
            binding.recyclerView.adapter = AccionAdapter(acciones){ accion -> onClickAccion(accion)}
        }
    }

    fun onClickBtnLista(numLista: Int){
        if(binding.progressBar.visibility == View.INVISIBLE){
            binding.progressBar.visibility = View.VISIBLE
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        val socket = Socket()
                        socket.connect(InetSocketAddress(ip, 11223), 5000)
                        val outputStream = socket.getOutputStream()
                        var envio = String(numLista.toString().toByteArray(), Charsets.US_ASCII).toByteArray()
                        outputStream.write(envio)
                        val input = Scanner(socket.getInputStream())
                        var listaMovilJson = input.nextLine()
                        socket.close()
                        val listaAcciones = Gson().fromJson(listaMovilJson, ListaAcciones::class.java)
                        acciones = listaAcciones.acciones
                        binding.progressBar.visibility = View.INVISIBLE
                    }catch (e: Exception){
                        binding.progressBar.visibility = View.INVISIBLE
                    }
                }
                binding.recyclerView.adapter = AccionAdapter(acciones){ accion -> onClickAccion(accion)}
            }
        }else{
            Toast.makeText(context, "Ya se está cargando una lista", Toast.LENGTH_SHORT)
        }
    }



    fun salirLista(){
        val accion = FragmentListasAccionesDirections.actionFragmentListasAccionesToFragmentConexion()
        navController.navigate(accion)
    }

}