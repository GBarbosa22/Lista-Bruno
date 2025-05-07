package com.example.projeto

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var textoPergunta: TextView
    private lateinit var botao1: Button
    private lateinit var botao2: Button
    private lateinit var botao3: Button

    private val database = FirebaseDatabase.getInstance()
    private val enqueteRef = database.getReference("enquetes/enquete1")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textoPergunta = findViewById(R.id.textPergunta)
        botao1 = findViewById(R.id.botaoOpcao1)
        botao2 = findViewById(R.id.botaoOpcao2)
        botao3 = findViewById(R.id.botaoOpcao3)

        enqueteRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pergunta = snapshot.child("pergunta").getValue(String::class.java)
                val opcoes = snapshot.child("opcoes")

                textoPergunta.text = pergunta

                botao1.text = "${opcoes.child("opcao1").child("titulo").value} (${opcoes.child("opcao1").child("votos").value})"
                botao2.text = "${opcoes.child("opcao2").child("titulo").value} (${opcoes.child("opcao2").child("votos").value})"
                botao3.text = "${opcoes.child("opcao3").child("titulo").value} (${opcoes.child("opcao3").child("votos").value})"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
            }
        })

        // Ações de voto
        botao1.setOnClickListener { votar("opcao1") }
        botao2.setOnClickListener { votar("opcao2") }
        botao3.setOnClickListener { votar("opcao3") }
    }

    private fun votar(opcao: String) {
        val votosRef = enqueteRef.child("opcoes").child(opcao).child("votos")
        votosRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val votosAtuais = currentData.getValue(Int::class.java) ?: 0
                currentData.value = votosAtuais + 1
                return Transaction.success(currentData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                if (committed) {
                    Toast.makeText(this@MainActivity, "Voto computado!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Erro ao votar!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
