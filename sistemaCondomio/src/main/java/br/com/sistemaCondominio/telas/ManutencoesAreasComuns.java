/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.sistemaCondominio.telas;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author laris
 */
public class ManutencoesAreasComuns extends javax.swing.JInternalFrame {

    // Modelo de dados para manutenções (em memória)
    private class Manutencao {
        int id;
        String area;
        String tipoProblema;
        String descricao;
        double custoEstimado;
        double custoFinal;
        String status;
        Date dataCriacao;
        Date dataAtualizacao;

        public Manutencao(int id, String area, String tipoProblema, String descricao, 
                         double custoEstimado, String status) {
            this.id = id;
            this.area = area;
            this.tipoProblema = tipoProblema;
            this.descricao = descricao;
            this.custoEstimado = custoEstimado;
            this.custoFinal = 0.0;
            this.status = status;
            this.dataCriacao = new Date();
            this.dataAtualizacao = new Date();
        }
    }

    private List<Manutencao> manutencoes;
    private int proximoId = 1;
    private DefaultTableModel tableModel;
    private NumberFormat currencyFormat;

    /**
     * Creates new form ManutencoesAreasComuns
     */
    public ManutencoesAreasComuns() {
        initComponents();
        manutencoes = new ArrayList<>();
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
        setupTable();
        populateComponents();
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
    }

    private void setupTable() {
        String[] colunas = {"ID", "Área", "Tipo Problema", "Descrição", "Custo Estimado", 
                           "Custo Final", "Status", "Data Criação"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTableManutencoes.setModel(tableModel);
        jTableManutencoes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    }

    private void populateComponents() {
        // Popula combo de áreas
        jComboBoxArea.addItem("Piscina");
        jComboBoxArea.addItem("Churrascaria");
        jComboBoxArea.addItem("Sala de Festas");

        // Popula combo de tipos de problema
        jComboBoxTipoProblema.addItem("Elétrica");
        jComboBoxTipoProblema.addItem("Hidráulica");
        jComboBoxTipoProblema.addItem("Estrutural");
        jComboBoxTipoProblema.addItem("Pintura");
        jComboBoxTipoProblema.addItem("Limpeza");
        jComboBoxTipoProblema.addItem("Outros");

        // Popula combo de status
        jComboBoxStatus.addItem("Aberto");
        jComboBoxStatus.addItem("Em andamento");
        jComboBoxStatus.addItem("Concluído");

        atualizarTabela();
    }

    private void atualizarTabela() {
        tableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        for (Manutencao m : manutencoes) {
            Object[] row = {
                m.id,
                m.area,
                m.tipoProblema,
                m.descricao.length() > 30 ? m.descricao.substring(0, 30) + "..." : m.descricao,
                currencyFormat.format(m.custoEstimado),
                m.custoFinal > 0 ? currencyFormat.format(m.custoFinal) : "-",
                m.status,
                dateFormat.format(m.dataCriacao)
            };
            tableModel.addRow(row);
        }
    }

    private boolean areaEmManutencao(String area) {
        for (Manutencao m : manutencoes) {
            if (m.area.equals(area) && 
                (m.status.equals("Aberto") || m.status.equals("Em andamento"))) {
                return true;
            }
        }
        return false;
    }

    private void novaManutencaoActionPerformed(java.awt.event.ActionEvent evt) {
        String area = (String) jComboBoxArea.getSelectedItem();
        String tipoProblema = (String) jComboBoxTipoProblema.getSelectedItem();
        String descricao = jTextAreaDescricao.getText().trim();
        
        // Validação
        if (area == null || area.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma área.");
            return;
        }
        
        if (tipoProblema == null || tipoProblema.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione o tipo de problema.");
            return;
        }
        
        if (descricao == null || descricao.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha a descrição do problema.");
            return;
        }

        // Verifica se área já está em manutenção
        if (areaEmManutencao(area)) {
            JOptionPane.showMessageDialog(this, 
                "Atenção! A área " + area + " já possui uma manutenção em aberto ou em andamento.",
                "Área em Manutenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtém custo estimado
        double custoEstimado = 0.0;
        try {
            String custoText = jTextFieldCustoEstimado.getText().trim().replace(",", ".");
            if (!custoText.isEmpty()) {
                custoEstimado = Double.parseDouble(custoText);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Custo estimado inválido. Use apenas números.");
            return;
        }

        // Cria nova manutenção
        Manutencao novaManutencao = new Manutencao(proximoId++, area, tipoProblema, descricao, 
                                                   custoEstimado, "Aberto");
        manutencoes.add(novaManutencao);
        
        atualizarTabela();
        limparFormulario();
        
        JOptionPane.showMessageDialog(this, "Manutenção cadastrada com sucesso!");
    }

    private void atualizarStatusActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = jTableManutencoes.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma manutenção na tabela.");
            return;
        }

        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        Manutencao manutencao = buscarManutencaoPorId(id);
        
        if (manutencao == null) {
            JOptionPane.showMessageDialog(this, "Manutenção não encontrada.");
            return;
        }

        String novoStatus = (String) jComboBoxStatus.getSelectedItem();
        double novoCustoFinal = 0.0;
        
        try {
            String custoText = jTextFieldCustoFinal.getText().trim().replace(",", ".");
            if (!custoText.isEmpty()) {
                novoCustoFinal = Double.parseDouble(custoText);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Custo final inválido. Use apenas números.");
            return;
        }

        manutencao.status = novoStatus;
        if (novoCustoFinal > 0) {
            manutencao.custoFinal = novoCustoFinal;
        }
        manutencao.dataAtualizacao = new Date();

        atualizarTabela();
        limparFormulario();
        
        JOptionPane.showMessageDialog(this, "Status atualizado com sucesso!");
    }

    private Manutencao buscarManutencaoPorId(int id) {
        for (Manutencao m : manutencoes) {
            if (m.id == id) {
                return m;
            }
        }
        return null;
    }

    private void limparFormulario() {
        jTextAreaDescricao.setText("");
        jTextFieldCustoEstimado.setText("");
        jTextFieldCustoFinal.setText("");
        jComboBoxArea.setSelectedIndex(0);
        jComboBoxTipoProblema.setSelectedIndex(0);
        jComboBoxStatus.setSelectedIndex(0);
        jTableManutencoes.clearSelection();
    }

    private void gerarRelatorioActionPerformed(java.awt.event.ActionEvent evt) {
        if (manutencoes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há manutenções cadastradas para gerar relatório.");
            return;
        }

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("=== RELATÓRIO DE MANUTENÇÕES ===\n\n");
        
        int totalAberto = 0, totalEmAndamento = 0, totalConcluido = 0;
        double totalCustoEstimado = 0.0, totalCustoFinal = 0.0;
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (Manutencao m : manutencoes) {
            relatorio.append(String.format("ID: %d\n", m.id));
            relatorio.append(String.format("Área: %s\n", m.area));
            relatorio.append(String.format("Tipo: %s\n", m.tipoProblema));
            relatorio.append(String.format("Descrição: %s\n", m.descricao));
            relatorio.append(String.format("Custo Estimado: %s\n", currencyFormat.format(m.custoEstimado)));
            relatorio.append(String.format("Custo Final: %s\n", 
                m.custoFinal > 0 ? currencyFormat.format(m.custoFinal) : "Não informado"));
            relatorio.append(String.format("Status: %s\n", m.status));
            relatorio.append(String.format("Data Criação: %s\n", dateFormat.format(m.dataCriacao)));
            relatorio.append(String.format("Última Atualização: %s\n", dateFormat.format(m.dataAtualizacao)));
            relatorio.append("----------------------------------------\n");
            
            if (m.status.equals("Aberto")) totalAberto++;
            else if (m.status.equals("Em andamento")) totalEmAndamento++;
            else if (m.status.equals("Concluído")) totalConcluido++;
            
            totalCustoEstimado += m.custoEstimado;
            totalCustoFinal += m.custoFinal;
        }
        
        relatorio.append("\n=== RESUMO ===\n");
        relatorio.append(String.format("Total de Manutenções: %d\n", manutencoes.size()));
        relatorio.append(String.format("Abertas: %d\n", totalAberto));
        relatorio.append(String.format("Em Andamento: %d\n", totalEmAndamento));
        relatorio.append(String.format("Concluídas: %d\n", totalConcluido));
        relatorio.append(String.format("Total Custo Estimado: %s\n", currencyFormat.format(totalCustoEstimado)));
        relatorio.append(String.format("Total Custo Final: %s\n", currencyFormat.format(totalCustoFinal)));

        JOptionPane.showMessageDialog(this, relatorio.toString(), "Relatório de Manutenções", 
                                     JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jComboBoxArea = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jComboBoxTipoProblema = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaDescricao = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldCustoEstimado = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldCustoFinal = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jComboBoxStatus = new javax.swing.JComboBox<>();
        jButtonNovaManutencao = new javax.swing.JButton();
        jButtonAtualizarStatus = new javax.swing.JButton();
        jButtonGerarRelatorio = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableManutencoes = new javax.swing.JTable();

        setTitle("Gerenciamento de Manutenções");

        jLabel1.setText("Área:");

        jLabel2.setText("Tipo de Problema:");

        jLabel3.setText("Descrição:");

        jTextAreaDescricao.setColumns(20);
        jTextAreaDescricao.setRows(5);
        jScrollPane1.setViewportView(jTextAreaDescricao);

        jLabel4.setText("Custo Estimado (R$):");

        jLabel5.setText("Custo Final (R$):");

        jLabel6.setText("Status:");

        jButtonNovaManutencao.setText("Nova Manutenção");
        jButtonNovaManutencao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                novaManutencaoActionPerformed(evt);
            }
        });

        jButtonAtualizarStatus.setText("Atualizar Status");
        jButtonAtualizarStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atualizarStatusActionPerformed(evt);
            }
        });

        jButtonGerarRelatorio.setText("Gerar Relatório");
        jButtonGerarRelatorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gerarRelatorioActionPerformed(evt);
            }
        });

        jTableManutencoes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTableManutencoes);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBoxArea, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBoxTipoProblema, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldCustoEstimado, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldCustoFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBoxStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonNovaManutencao)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonAtualizarStatus)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonGerarRelatorio)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBoxArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBoxTipoProblema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldCustoEstimado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jTextFieldCustoFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jComboBoxStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonNovaManutencao)
                    .addComponent(jButtonAtualizarStatus)
                    .addComponent(jButtonGerarRelatorio))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAtualizarStatus;
    private javax.swing.JButton jButtonGerarRelatorio;
    private javax.swing.JButton jButtonNovaManutencao;
    private javax.swing.JComboBox<String> jComboBoxArea;
    private javax.swing.JComboBox<String> jComboBoxStatus;
    private javax.swing.JComboBox<String> jComboBoxTipoProblema;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableManutencoes;
    private javax.swing.JTextArea jTextAreaDescricao;
    private javax.swing.JTextField jTextFieldCustoEstimado;
    private javax.swing.JTextField jTextFieldCustoFinal;
    // End of variables declaration//GEN-END:variables
}

