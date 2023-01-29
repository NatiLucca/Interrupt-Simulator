/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorinterrupcoes;

import java.util.*;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author nadja e natiele
 */
public class Table extends AbstractTableModel{
    
    private List<Quadros> quadros = new ArrayList<>();
    
    public Table() {
    }

    @Override
    public int getRowCount() {
        return quadros.size();
    }

    @Override
    public int getColumnCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getValueAt(int linha, int coluna) {
         switch(coluna){
            case 0:
                return quadros.get(linha).getnQuadro();
            case 1:
                return quadros.get(linha).getQuadro();
            case 2: 
                return quadros.get(linha).getBitValidade();
        }
        return null;
    }
   
    @Override
    public void setValueAt(Object x, int linha, int coluna){
        switch(coluna){
            case 0:
                quadros.get(linha).setnQuadro(Integer.parseInt((String) x));
                break;
            case 1:
                quadros.get(linha).setQuadro(Integer.parseInt((String) x));
                break;
            case 2: 
                quadros.get(linha).setBitValidade(Integer.parseInt((String) x));
                break;
        }
        
        this.fireTableRowsUpdated(linha, linha);
    }
    
    public void add(Quadros q){
        this.quadros.add(q);
        this.fireTableDataChanged();
    }
    
    public void remove(int linha){
        this.quadros.remove(linha);
        this.fireTableRowsDeleted(linha, linha);
    }
}
