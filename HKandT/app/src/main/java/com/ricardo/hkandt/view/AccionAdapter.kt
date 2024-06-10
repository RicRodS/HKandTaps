package com.ricardo.hkandt.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ricardo.hkandt.R
import com.ricardo.hkandt.databinding.ItemAccionBinding
import com.ricardo.hkandt.model.Accion

class AccionAdapter(private val listaAcciones: List<Accion>,
                    private val onClickAccion: (Accion) -> Unit)
                    : RecyclerView.Adapter<AccionAdapter.AccionViewHolder>()
{
    class AccionViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val binding = ItemAccionBinding.bind(view)

        fun bind(accion: Accion, onClickAccion: (Accion) -> Unit){
            binding.btnNombreAccion.text = accion.nombre
            binding.btnNombreAccion.setOnClickListener { onClickAccion(accion) }
            binding.layout.setOnClickListener { onClickAccion(accion) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccionViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_accion, parent, false)
        return AccionViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: AccionViewHolder, position: Int) {
        val accion = listaAcciones[position]
        holder.bind(accion, onClickAccion)
    }

    override fun getItemCount(): Int = listaAcciones.size

}