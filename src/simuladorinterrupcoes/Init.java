/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorinterrupcoes;
import java.util.Random;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author nadja e natiele
 */
public class Init extends javax.swing.JFrame {
    
    DefaultListModel lista = new DefaultListModel(); // PRONTOS
    DefaultListModel l = new DefaultListModel(); // ESPERA    
    Random gerador = new Random(); 
    int QtPag=0, Npag=0;
    private int index=0, TamMem=32, TamPag=11, TamTLB=5, TamSwap=32, MaxProcesso=12;
  
    /**
     * Creates new form Init
     */
    public Init() {
        initComponents();
        TLB();
        ba.setVisible(false);
        acao.setVisible(false);
        execut.setVisible(false);
        GeraProcessos(1);       
    }
    private void TLB(){ //redimenciona a tlb
        tlb.getColumnModel().getColumn(0).setMaxWidth(90);
        tlb.getColumnModel().getColumn(1).setMaxWidth(70);
        tlb.getColumnModel().getColumn(2).setMaxWidth(30);
        tlb.getColumnModel().getColumn(3).setMaxWidth(100);
    }
    private void GeraProcessos(int x){ 
  
        for (int i=x; i < 13; i++){
            lista.addElement("Processo " + i);                
        }        
        prontos.setModel(lista);
    }
   
    public void ProcessosCPU(){
       cpu.setText(prontos.getModel().getElementAt(0));       
       
       int x = gerador.nextInt(5);
       if(x == 0 || x == 1){//GEROU UMA INTERRUPÇÃO ou E/S
            if(x == 0){
                JOptionPane.showMessageDialog(null, "Interrupção de Sistema!");
            }else{
                JOptionPane.showMessageDialog(null, "Operação de E/S!");
            }            
            l.addElement(cpu.getText());              
            espera.setModel(l);
            cpu.setText("");        
            lista.removeElementAt(0);
            ba.setVisible(false);
            be.setVisible(true);            
        }else{ //PROCESSO NORMAL         
            lista.removeElementAt(0);
            ba.setVisible(true);
            be.setVisible(false);            
       }
       
       ((DefaultTableModel) tabPag.getModel()).setNumRows(0);
       tabPag.updateUI();
    }
    
    public void AlocaProcessos(){
        // SORTEIA A QUANTIDADE DE PAGINAS PARA UM PROCESSO X    
        Npag = ((gerador.nextInt(MaxProcesso)) + 4);       
       
        DefaultTableModel dtMem = (DefaultTableModel) MemP.getModel();
        DefaultTableModel dtPag = (DefaultTableModel) tabPag.getModel(); 
        DefaultTableModel dtTlb = (DefaultTableModel) tlb.getModel();       
       
       // ATUALIZA O BIT DE VALIDADE DA MEMORIA PRINCIPAL
        for(int a=0; a<dtMem.getRowCount(); a++){
             dtMem.setValueAt(1, a, 2);
        }
         
        // ATUALIZA O BIT DE VALIDADE DA TLB
        for(int a=0; a<dtTlb.getRowCount(); a++){
             dtTlb.setValueAt(1, a, 2);
        }
        
        for(int j=0; j<Npag; j++){ // PARA CADA PAGINA
            
                    if(index <= TamMem){ // ALOCA A MEMORIA PRINCIPAL                                              
                        dtMem.addRow(new Object[]{index,j, 0});
                        MemP.setModel(dtMem);                        

                    }else{// SUBSTITUIÇÃO DE QUADROS DA MEMORIA                        
                        int z, px;
                        //PARA GARANTIR QUE NAO VAI SUBSTITUIR UMA PAGINA DO PROCESSO ATUAL
                        do{
                            z = gerador.nextInt(TamMem);  
                          
                            px = (int) dtMem.getValueAt(z, 2);                            
                        }while( px != 1);
                        // COPIA QUADRO DA MEMORIA PARA SWAP 
                       
                            AlocaSwap(((int)dtMem.getValueAt(z, 0)), ((int)dtMem.getValueAt(z, 1)), 1);
                            // ATUALIZA QUADRO COM NOVAS INFORMAÇÕES 
                            dtMem.setValueAt(index, z, 0); // Nqaudro
                            dtMem.setValueAt(j, z, 1); // quadro
                            dtMem.setValueAt(0, z, 2); // bit
                    } 
                    
                    if(j < TamPag){//ALOCA A TABELA DE PAGINAS     
                            AlocaTabPag(j, index);                            
                    }
                    index++;
        }
        ba.setVisible(false);
        execut.setVisible(true);
              
        if(l.size() > 0){ //TIRA PROCESSO DA FILA DE ESPERA -> PRONTO
            lista.addElement(l.getElementAt(0));
            prontos.setModel(lista);
            l.removeElementAt(0);
        }  
        
         QtPag = gerador.nextInt(Npag);
         if(QtPag == 0){
             QtPag++;
         }
    }
    
    public void AlocaTLB(int a, int b){
        DefaultTableModel dtTlb = (DefaultTableModel) tlb.getModel();
      
        if(dtTlb.getRowCount() < TamTLB){ // se a tlb nao esta vazia -> insere
            dtTlb.addRow(new Object[]{a, b, 0, 0});
            tlb.setModel(dtTlb);
        }else{            
            //int lin = gerador.nextInt(TamTLB); 
            int lin=0, max=-1, aux=0;
            for(int i=0; i <dtTlb.getRowCount(); i++){
                aux = (int) dtTlb.getValueAt(i, 3);
                if(max < aux){
                    lin = i;
                    max = aux;
                }                
            }
            dtTlb.setValueAt(a, lin, 0); // Npagina
            dtTlb.setValueAt(b, lin, 1); // quadro
            dtTlb.setValueAt(0, lin, 2); // bit
            dtTlb.setValueAt(0, lin, 3); // penalização
        } 
        tlb.updateUI();
    }
    
    public void AlocaSwap(int a, int b, int bit){
            DefaultTableModel dtSwap = (DefaultTableModel) swap.getModel(); 
            if(dtSwap.getRowCount() < TamSwap){
                dtSwap.addRow(new Object[]{a, b, bit});
                swap.setModel(dtSwap);  
            }else{
                int lin = gerador.nextInt(TamSwap);//ATUALIZA VALORES DA LINHA            
                dtSwap.setValueAt(a, lin, 0); // Npagina
                dtSwap.setValueAt(b, lin, 1); // quadro
                dtSwap.setValueAt(bit, lin, 2); // bit
            }
            swap.updateUI();
    }
    
    public void AlocaTabPag(int a, int b){ 
        DefaultTableModel dtpag = (DefaultTableModel) tabPag.getModel();
        // se a tabela de paginas nao esta vazia -> insere
        if(dtpag.getRowCount() < TamPag){
            dtpag.addRow(new Object[]{a, b});
            tabPag.setModel(dtpag);
        }else{ // se a tabela de paginas esta cheia -> substitui
            int lin = gerador.nextInt(TamPag);//ATUALIZA VALORES DA LINHA            
            dtpag.setValueAt(a, lin, 0); // Npagina
            dtpag.setValueAt(b, lin, 1); // quadro
        }   
        tabPag.updateUI();
    }    
    
    public int AcessaPaginas(){
        DefaultTableModel dtTlb = (DefaultTableModel) tlb.getModel();
        acao.setVisible(true);
        if(QtPag > 0){            
                 
            //SORTEAR A PAGINA
            int y = gerador.nextInt(Npag); 
            if( y < 2){
                y += 2;
            }
            cpu.setText("Página " + y);  
            QtPag--;         

             // ATUALIZA A PENALIZAÇÃO DA TLB
             int aux=0;
                    for(int a=0; a<dtTlb.getRowCount(); a++){
                        aux = ((int) dtTlb.getValueAt(a, 3));
                        aux++;
                        dtTlb.setValueAt(aux, a, 3);
                       // JOptionPane.showMessageDialog(null, "paaaaa");
                    } 
            // VERIFICA SE ESTA NA TLB
            if(tlb.getRowCount() != 0){ // NAO ESTA VAZIA -> VERIFICA
                for(int id=0; id<tlb.getRowCount(); id++){
                    if((((int)tlb.getValueAt(id, 0)) == y) && (((int)tlb.getValueAt(id, 2)) == 0)){                       
                           acao.setText("TLB -> Rit"); 
                           dtTlb.setValueAt(0, id, 3);
                          // tlb.setBackground(Color.red);
                        
                           return 1;
                    }                   
                }               
            }else{acao.setText("TLB -> Miss");}

            // VERIFICA SE ESTA NA TABELA DE PAGINAS
            if(tabPag.getRowCount() != 0){ // Com Linhas -> VERIFICA
                    for(int id=0; id<tabPag.getRowCount(); id++){
                            if(((int)tabPag.getValueAt(id, 0)) == y){                       
                                    acao.setText("Tabela de Páginas -> Rit");
                                    AlocaTLB(((int)tabPag.getValueAt(id, 0)),((int)tabPag.getValueAt(id, 1)));
                                    return 1;
                            }
                   }              
            }else{acao.setText("Tabela de Páginas -> Miss");}

            // VERIFICA NA MEMORIA
            if(MemP.getRowCount() != 0){ // NAO ESTA VAZIA -> VERIFICA
                    for(int id=0; id<MemP.getRowCount(); id++){
                            if((((int)MemP.getValueAt(id, 1)) == y) && (((int)MemP.getValueAt(id, 2)) == 0)){                       
                                    acao.setText("Memória -> Rit");                                                                                
                                    int a = ((int)MemP.getValueAt(id, 1));
                                    int b = ((int)MemP.getValueAt(id, 0));
                                    AlocaTabPag(a, b);
                                    AlocaTLB(a, b);
                                    return 1;
                            }
                    }      
            }
        }else{
                execut.setVisible(false);
                be.setVisible(true);
                acao.setText("");  
                cpu.setText(""); 
        }
        
        return 1;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        MemP = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tlb = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabPag = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        swap = new javax.swing.JTable();
        cpu = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        prontos = new javax.swing.JList<>();
        jScrollPane6 = new javax.swing.JScrollPane();
        espera = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        be = new javax.swing.JButton();
        ba = new javax.swing.JButton();
        execut = new javax.swing.JButton();
        acao = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();

        setTitle("Simulador de Memória Virtual");
        setResizable(false);

        MemP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nº Quadro", "Quadro", "Bit Validade"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(MemP);
        if (MemP.getColumnModel().getColumnCount() > 0) {
            MemP.getColumnModel().getColumn(0).setResizable(false);
            MemP.getColumnModel().getColumn(1).setResizable(false);
            MemP.getColumnModel().getColumn(2).setResizable(false);
        }

        tlb.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tlb.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nº Página", "Quadro", "Bit", "Penalização"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tlb.setColumnSelectionAllowed(true);
        jScrollPane3.setViewportView(tlb);
        tlb.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (tlb.getColumnModel().getColumnCount() > 0) {
            tlb.getColumnModel().getColumn(0).setResizable(false);
            tlb.getColumnModel().getColumn(1).setResizable(false);
            tlb.getColumnModel().getColumn(2).setResizable(false);
            tlb.getColumnModel().getColumn(3).setResizable(false);
        }

        tabPag.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nº Página", "Quadro"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tabPag);
        if (tabPag.getColumnModel().getColumnCount() > 0) {
            tabPag.getColumnModel().getColumn(0).setResizable(false);
            tabPag.getColumnModel().getColumn(1).setResizable(false);
        }

        swap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nº Quadro", "Quadro", "Bit Validade"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(swap);
        if (swap.getColumnModel().getColumnCount() > 0) {
            swap.getColumnModel().getColumn(0).setResizable(false);
            swap.getColumnModel().getColumn(1).setResizable(false);
            swap.getColumnModel().getColumn(2).setResizable(false);
        }

        cpu.setEditable(false);
        cpu.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        prontos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                prontosMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(prontos);

        jScrollPane6.setViewportView(espera);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/computer_go.png"))); // NOI18N
        jLabel1.setText("CPU");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/application_form_add.png"))); // NOI18N
        jLabel2.setText("Prontos");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/application_form_delete.png"))); // NOI18N
        jLabel3.setText("Espera");

        be.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/application_go.png"))); // NOI18N
        be.setText("Executar");
        be.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                beItemStateChanged(evt);
            }
        });
        be.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                beActionPerformed(evt);
            }
        });

        ba.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/database_go.png"))); // NOI18N
        ba.setText("Alocar");
        ba.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                baActionPerformed(evt);
            }
        });

        execut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/control_end_blue.png"))); // NOI18N
        execut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executActionPerformed(evt);
            }
        });

        acao.setFont(new java.awt.Font("DejaVu Sans", 0, 15)); // NOI18N

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/application_view_icons.png"))); // NOI18N
        jLabel4.setText("TLB");

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/page.png"))); // NOI18N
        jLabel5.setText("Tabela de Páginas");

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/folder_page_white.png"))); // NOI18N
        jLabel6.setText("Swap");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/table_multiple.png"))); // NOI18N
        jLabel7.setText("Memória RAM");

        jTextField1.setEditable(false);
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.setText("Disco");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(93, 93, 93)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(145, 145, 145)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(execut, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(be, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ba, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(153, 153, 153)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(96, 96, 96)
                        .addComponent(cpu, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(120, 120, 120)
                        .addComponent(acao, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(104, 104, 104)
                                .addComponent(jLabel4))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(55, 55, 55)
                                .addComponent(jLabel5))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(104, 104, 104)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 49, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)))
                .addGap(103, 103, 103))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(194, 194, 194))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cpu, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(acao, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(11, 11, 11)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(41, 41, 41)
                                .addComponent(execut, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(be, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ba, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 70, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(33, 33, 33))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 483, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void beActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_beActionPerformed
        if((lista.size() != 0)){
            ProcessosCPU();
        }else if((lista.size() == 0) && (l.size() == 0)){
            JOptionPane.showMessageDialog(null, "Fila de Processos Vazia!");
            ba.setVisible(false);
            be.setVisible(false);
        }else{
            lista.addElement(l.getElementAt(0));
            prontos.setModel(lista);
            l.removeElementAt(0);
        }
    }//GEN-LAST:event_beActionPerformed

    private void baActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_baActionPerformed
        AlocaProcessos();
    }//GEN-LAST:event_baActionPerformed

    private void executActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executActionPerformed
        AcessaPaginas();
    }//GEN-LAST:event_executActionPerformed

    private void prontosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prontosMouseClicked
     
    }//GEN-LAST:event_prontosMouseClicked

    private void beItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_beItemStateChanged
       
    }//GEN-LAST:event_beItemStateChanged

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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Init.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Init().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable MemP;
    private javax.swing.JLabel acao;
    private javax.swing.JButton ba;
    private javax.swing.JButton be;
    private javax.swing.JTextField cpu;
    private javax.swing.JList<String> espera;
    private javax.swing.JButton execut;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JList<String> prontos;
    private javax.swing.JTable swap;
    private javax.swing.JTable tabPag;
    private javax.swing.JTable tlb;
    // End of variables declaration//GEN-END:variables
}
