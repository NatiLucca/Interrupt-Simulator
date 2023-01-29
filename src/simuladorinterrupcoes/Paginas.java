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
public class Paginas {
    private int NumPagina;
    private int NumQuadro;

    public Paginas(int NumPagina, int NumQuadro) {
        this.NumPagina = NumPagina;
        this.NumQuadro = NumQuadro;
    }
    
    public int getNumPagina() {
        return NumPagina;
    }

    public int getNumQuadro() {
        return NumQuadro;
    }

    public void setNumPagina(int NumPagina) {
        this.NumPagina = NumPagina;
    }

    public void setNumQuadro(int NumQuadro) {
        this.NumQuadro = NumQuadro;
    }
    
}
