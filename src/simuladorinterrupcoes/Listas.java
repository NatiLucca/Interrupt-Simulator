/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorinterrupcoes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author natiele
 */
public class Listas {
    
    List<Processo> processos = new ArrayList<>(); //processos
    
  
    
    public Listas() {
        
    }
    
    public Listas(Processo p) {
        processos.add(p);
    }
    
    public List<Processo> ListaProcessos(){       
        return processos;
    }
    
    
}



    
  