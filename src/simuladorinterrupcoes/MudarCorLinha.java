/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorinterrupcoes;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author nadja e natiele
 */
public class MudarCorLinha extends DefaultTableCellRenderer {
    
    private int id;
    
    public MudarCorLinha() {
        
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int linha, int coluna) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, linha, coluna);
        
        if(isSelected){
            setBackground(Color.green);
        }
       
        if(id == linha){
            setBackground(Color.green);
        }else{
            setBackground(null);
        }
            
        return this;
    }
    
    public void setId(int i){
        this.id = i;
    }
    
    
}
