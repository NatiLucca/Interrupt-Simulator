/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorinterrupcoes;


/**
 *
 * @author nadja e natiele
 */
public class Processo {
        private int Xbytes; // tamanho bytes
        private int num; // ID DO PROCESSO
        private boolean conclusion; // concluido ou em execução
        private int prioridade; // prioridade decrescente
        private int situacao; 
        /* 
        Situação:
           0. Pronto
           1. Bloqueado E/S
           2. Bloqueado Arquivo
           3. Bloqueado Rede            
        */
               
    public Processo() {
   
    }
    
    public Processo(int Xbytes, int i, int p) {
        this.num = i;
        this.Xbytes = Xbytes;
        this.prioridade = p;
        this.conclusion = false;
        this.situacao = 0;
    }

    public Processo(int Xbytes, int i, int p, int s) {
        this.num = i;
        this.Xbytes = Xbytes;
        this.prioridade = p;
        this.conclusion = true;
        this.situacao = 0;
    }
    
    public int getXbytes() {
        return this.Xbytes;
    }
    
    public int getSituacao() {
        return this.situacao;
    }
    
    public int getPrioridade() {
        return this.prioridade;
    }

    public void setXbytes(int Xbytes) {
        this.Xbytes = Xbytes;
    }
        
    public int getNum(){
        return this.num;
    }  
    
    public boolean getConclusion(){
        return this.conclusion;
    }
    
    public void setConclusion(){
        this.conclusion = true;
    }
    
    public void setSituacao(int p) {
        this.situacao = p;
    }
}
