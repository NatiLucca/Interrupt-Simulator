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
public class Quadros {
    private int nQuadro;
    private int quadro; //VALOR DO QUADRO
    private int bitValidade;

    public Quadros(){}
    
    public Quadros(int nQuadro, int quadro) {
        this.nQuadro = nQuadro; 
        this.quadro = quadro;
        this.bitValidade = 0;
    }

    public int getnQuadro() {
        return nQuadro;
    }

    public int getQuadro() {
        return quadro;
    }

    public void setnQuadro(int nQuadro) {
        this.nQuadro = nQuadro;
    }

    public void setQuadro(int quadro) {
        this.quadro = quadro;
    }

   public void setBitValidade(int bitValidade) {
        this.bitValidade = bitValidade;
    }

    public int getBitValidade() {
        return bitValidade;
    }
    
    
    
    
}
