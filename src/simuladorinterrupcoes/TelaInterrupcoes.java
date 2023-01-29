/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorinterrupcoes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author natiele
 */
public class TelaInterrupcoes extends javax.swing.JFrame {
    /**
     * Creates new form TelaInterrupcoes
     */
  
    DefaultListModel listaProntos = new DefaultListModel(); // Prontos
    DefaultListModel listaEspera = new DefaultListModel(); // Espera
    DefaultListModel listaES = new DefaultListModel(); // Entrada e Saída
    DefaultListModel listaArq = new DefaultListModel(); // Arquivos
    DefaultListModel listaRede = new DefaultListModel(); // Rede
    List<Processo> processos = new ArrayList<>(); //processos
    
    public TelaInterrupcoes() throws InterruptedException {
        
        initComponents();

        FluxoThread t1 = new FluxoThread(); // thread cpu
        FluxoInterrupcoes thread2 = new FluxoInterrupcoes(); // thread controladora E/S
        FluxoInterrupcoesRede t3 = new FluxoInterrupcoesRede(); // thread controladora arquivos
        FluxoInterrupcoesArq  t4 = new FluxoInterrupcoesArq(); // thread controladora rede
        t1.start();        
        thread2.start();
        t3.start();
        t4.start();
    }

    public void GeraProcessos(){ 
        try{
                // Cria o processo e adiciona na lista de processos
                Random random = new Random();  
                Processo j = new Processo(0, 0, 0, 0);
                processos.add(j);
                for (int i=1; i < 21; i++){
                    int prioridade = random.nextInt(10); // prioridade
                    int tam = random.nextInt(100);  // tamanho 
                    tam += 100;                    
                    Processo a = new Processo(tam, i, prioridade);
                    processos.add(a);
                    listaProntos.addElement("Processo " + i); 
                }        
                prontos.setModel(listaProntos);
        }catch(Exception ex ){            
            JOptionPane.showMessageDialog(null, "Erro " + ex);
        }  
    }
    
    
    // controla cpu           
    public class FluxoThread extends Thread{
        Random random = new Random(); 
        private int count = 0;
        public void run(){
            int n=0;
            GeraProcessos();
            textoINter.setVisible(false);
          
            while(true){
                try {                
                    carregaCpu();    

                } catch (InterruptedException ex) {
                    Logger.getLogger(TelaInterrupcoes.class.getName()).log(Level.SEVERE, null, ex);
                }
            
            }
        } 
        
        public void TrocaContextoCpu() throws InterruptedException{
            int val = random.nextInt(2000);
            val += 1500;
            
            if(listaEspera.size() != 0){
                 
                 if((val < 3 || val > 7) && (!cpu.getText().isEmpty())){ // troca de contexto
                     listaProntos.addElement(cpu.getText());                        
                     prontos.setModel(listaProntos);
                     cpu.setText(bloqueados.getModel().getElementAt(0)); 
                     
                     
                 }else{ // processo 
                     cpu.setText(bloqueados.getModel().getElementAt(0));  
                 }
                 listaEspera.removeElementAt(0);
                 textoINter.setVisible(false);
            }
            Thread.sleep(val);
        }
        public void carregaCpu() throws InterruptedException{
            
           while(listaProntos.size() != 0){ // enquanto ainda há processos
                int val = random.nextInt(12); 
                int temp = 3000;
                TrocaContextoCpu();
                                
                if(val == 0 || val == 4 || val > 7){ // Processo normal -> CPU
                        // inicia cpu com o topo da lista de prontos
                        cpu.setText(prontos.getModel().getElementAt(0));                
                        drive.setBackground(Color.white);
                        textoINter.setVisible(false);
                        temp = 5000;
             
                }else if(val == 1 || val == 7){ // Interrupção -> E/S
                        cpu.setText("");
                        listaES.addElement("" + prontos.getModel().getElementAt(0)); 
                        es.setModel(listaES);                
                        drive.setBackground(Color.blue);
                        temp = 0;
                }else if(val == 2 || val == 5){ // Interrupção -> arquivo
                        listaArq.addElement("" + prontos.getModel().getElementAt(0)); 
                        arq.setModel(listaArq);
                        cpu.setText("");
                        drive.setBackground(Color.green);
                        temp = 0;
                }else if(val == 3 || val == 6){ // Interrupção -> rede
                        cpu.setText("");
                        listaRede.addElement("" + prontos.getModel().getElementAt(0)); 
                        red.setModel(listaRede);
                        drive.setBackground(Color.yellow);
                        temp = 0;
                }
                
               listaProntos.removeElementAt(0); // tira o topo da lista de prontos
               try { 
                    Thread.sleep (temp);                
                } catch (InterruptedException ex) {}
                //listaProntos.removeElementAt(0); // tira o topo da lista de prontos
               
            }
           
            while(listaEspera.size() != 0){     
                textoINter.setVisible(false);
                TrocaContextoCpu();
            }
            
            cpu.setText("");// finalizou execução
            drive.setBackground(Color.white);
        }// end carrega cpu
               
    }// end fluxo   
        public class FluxoInterrupcoes extends Thread{
            Random random = new Random();  
            int aux1=0, aux2=0, aux3=0;
            String temp;
            public void run(){
                    while(true){                        
                            if(listaES.size() != 0){
                                IntES.setText("" + es.getModel().getElementAt(0));
                                temp = es.getModel().getElementAt(0);
                                listaES.removeElementAt(0);                        
                                aux1 = random.nextInt(1000); 
                                aux1 += 7000;
                               try { 
                                    Thread.sleep (aux1);                
                                } catch (InterruptedException ex) {}                       
                                listaEspera.addElement("" + temp); 
                                textoINter.setVisible(true);
                                IntES.setText("");
                            }
                            bloqueados.setModel(listaEspera);
                    }
            }
        }
        
        public class FluxoInterrupcoesArq extends Thread{
            Random random = new Random();  
            int aux2=0;
            String temp;
            public void run(){
                    while(true){                        
                           if(listaArq.size() != 0){
                                IntA.setText("" + arq.getModel().getElementAt(0));
                                temp = arq.getModel().getElementAt(0);
                                listaArq.removeElementAt(0);
                                aux2 = random.nextInt(1000); 
                                aux2 += 7000;
                                try { 
                                    Thread.sleep (aux2);                
                                } catch (InterruptedException ex) {}
                                textoINter.setVisible(true);
                                listaEspera.addElement("" + temp);
                                IntA.setText("" );
                            }  
                            bloqueados.setModel(listaEspera);
                    }
            }
        }
        
        public class FluxoInterrupcoesRede extends Thread{
            Random random = new Random();  
            int aux3=0;
            String temp;
            public void run(){
                    while(true){                      
                            if(listaRede.size() != 0){
                                IntR.setText("" + red.getModel().getElementAt(0));
                                temp = red.getModel().getElementAt(0);
                                listaRede.removeElementAt(0);
                                aux3 = random.nextInt(1000); 
                                aux3 +=7000;
                                try { 
                                    Thread.sleep (aux3);                
                                } catch (InterruptedException ex) {}
                                listaEspera.addElement("" + temp); 
                                textoINter.setVisible(true);
                                IntR.setText("");
                            }
                        bloqueados.setModel(listaEspera);
                    }
            }
        }
     
      
  
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        prontos = new javax.swing.JList<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        bloqueados = new javax.swing.JList<>();
        cpu = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        IntES = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        es = new javax.swing.JList<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        arq = new javax.swing.JList<>();
        jScrollPane5 = new javax.swing.JScrollPane();
        red = new javax.swing.JList<>();
        jLabel7 = new javax.swing.JLabel();
        drive = new javax.swing.JTextField();
        IntA = new javax.swing.JTextField();
        IntR = new javax.swing.JTextField();
        textoINter = new javax.swing.JLabel();
        sair = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setTitle("Interrupções");
        setResizable(false);

        jScrollPane1.setViewportView(prontos);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/application_go.png"))); // NOI18N
        jLabel2.setText("Prontos");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/application_form_magnify.png"))); // NOI18N
        jLabel3.setText("Bloqueados");

        jScrollPane2.setViewportView(bloqueados);

        cpu.setEditable(false);
        cpu.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel1.setText("CPU");

        IntES.setEditable(false);
        IntES.setBackground(new java.awt.Color(45, 102, 214));
        IntES.setForeground(new java.awt.Color(12, 13, 14));

        jLabel4.setBackground(new java.awt.Color(96, 206, 244));
        jLabel4.setForeground(new java.awt.Color(21, 23, 29));
        jLabel4.setText("Entrada & Saída");

        jScrollPane3.setViewportView(es);

        jLabel5.setBackground(new java.awt.Color(174, 242, 163));
        jLabel5.setForeground(new java.awt.Color(8, 20, 8));
        jLabel5.setText("Arquivos");

        jLabel6.setBackground(new java.awt.Color(247, 239, 105));
        jLabel6.setForeground(new java.awt.Color(27, 15, 15));
        jLabel6.setText("Rede");

        arq.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { " " };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(arq);

        jScrollPane5.setViewportView(red);

        jLabel7.setFont(new java.awt.Font("Ubuntu", 2, 18)); // NOI18N
        jLabel7.setText("Controladoras");

        drive.setEditable(false);
        drive.setBackground(new java.awt.Color(251, 247, 243));
        drive.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        drive.setText("Driver");

        IntA.setBackground(new java.awt.Color(12, 191, 38));
        IntA.setForeground(new java.awt.Color(18, 24, 19));

        IntR.setBackground(new java.awt.Color(244, 226, 26));

        textoINter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/bullet_red.png"))); // NOI18N
        textoINter.setText("Interrupção");

        sair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/cancel.png"))); // NOI18N
        sair.setText("Sair");
        sair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sairActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/chart_organisation.png"))); // NOI18N
        jButton1.setText("Fluxograma");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(textoINter, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(95, 95, 95)
                                    .addComponent(jLabel1))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(41, 41, 41)
                                    .addComponent(cpu, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)))
                .addGap(26, 26, 26)
                .addComponent(drive, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(IntES, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
                        .addGap(46, 46, 46)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(IntA, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(IntR, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE))
                            .addComponent(jLabel6))
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addGap(30, 30, 30)
                                .addComponent(sair, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(137, 137, 137))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cpu, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textoINter, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel6)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(IntES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(IntA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(IntR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(24, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jScrollPane5)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(sair, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(25, 25, 25))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(drive, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void sairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sairActionPerformed
   
         Info a = new Info();
         a.setVisible(true);
         this.dispose();
    }//GEN-LAST:event_sairActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        Fluxograma flu = new Fluxograma();
        flu.setVisible(true);
        TelaInterrupcoes ads = null;

    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaInterrupcoes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaInterrupcoes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaInterrupcoes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaInterrupcoes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new TelaInterrupcoes().setVisible(true);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TelaInterrupcoes.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField IntA;
    private javax.swing.JTextField IntES;
    private javax.swing.JTextField IntR;
    private javax.swing.JList<String> arq;
    private javax.swing.JList<String> bloqueados;
    private javax.swing.JTextField cpu;
    private javax.swing.JTextField drive;
    private javax.swing.JList<String> es;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JList<String> prontos;
    private javax.swing.JList<String> red;
    private javax.swing.JButton sair;
    private javax.swing.JLabel textoINter;
    // End of variables declaration//GEN-END:variables
} // end tela interrupções