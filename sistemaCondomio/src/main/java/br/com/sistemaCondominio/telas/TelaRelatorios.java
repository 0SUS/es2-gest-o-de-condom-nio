package br.com.sistemaCondominio.telas;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;

public class TelaRelatorios extends JInternalFrame {

    private JComboBox<String> cmbTipoRelatorio;
    private JPanel panelFiltros;
    private JTextField txtDataInicio, txtDataFim, txtUnidade;
    private JComboBox<String> cmbStatus;
    private JButton btnGerar, btnExportarPDF;
    private JLabel lblStatusRelatorio, lblDataInicio, lblDataFim, lblUnidade, lblStatus;
    private JTable tblResultado;
    private JScrollPane scrollPaneResultado;

    public TelaRelatorios() {
        setTitle("Geração de Relatórios");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setBounds(100, 100, 800, 600);

        // --- Painel Principal ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        getContentPane().add(mainPanel);

        // --- 1. Seção de Seleção (Norte) ---
        JPanel panelSelecao = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSelecao.add(new JLabel("Selecione o Tipo de Relatório:"));
        cmbTipoRelatorio = new JComboBox<>(new String[]{
            "Selecione...", "Unidades", "Moradores", "Taxas de Condomínio", "Reservas", "Manutenções"
        });
        panelSelecao.add(cmbTipoRelatorio);
        mainPanel.add(panelSelecao, BorderLayout.NORTH);

        // --- 2. Seção de Filtros (Centro) ---
        panelFiltros = new JPanel();
        panelFiltros.setBorder(BorderFactory.createTitledBorder(null, "Filtros", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panelFiltros.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setVisible(false);

        lblDataInicio = new JLabel("Data Início:");
        txtDataInicio = new JTextField(10);
        lblDataFim = new JLabel("Data Fim:");
        txtDataFim = new JTextField(10);
        lblUnidade = new JLabel("Unidade:");
        txtUnidade = new JTextField(10);
        lblStatus = new JLabel("Status:");
        cmbStatus = new JComboBox<>();

        panelFiltros.add(lblDataInicio);
        panelFiltros.add(txtDataInicio);
        panelFiltros.add(lblDataFim);
        panelFiltros.add(txtDataFim);
        panelFiltros.add(lblUnidade);
        panelFiltros.add(txtUnidade);
        panelFiltros.add(lblStatus);
        panelFiltros.add(cmbStatus);
        mainPanel.add(panelFiltros, BorderLayout.CENTER);

        // --- 3. Seção de Resultados e Ações (Sul) ---
        JPanel panelAcoesResultados = new JPanel(new BorderLayout(10, 10));
        
        // Botões
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnGerar = new JButton("Gerar Relatório");
        btnGerar.setEnabled(false);
        btnExportarPDF = new JButton("Exportar PDF");
        btnExportarPDF.setEnabled(false);
        panelBotoes.add(btnGerar);
        panelBotoes.add(btnExportarPDF);
        
        lblStatusRelatorio = new JLabel("Nenhum registro encontrado.");
        lblStatusRelatorio.setForeground(Color.RED);
        lblStatusRelatorio.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatusRelatorio.setVisible(false);
        panelBotoes.add(lblStatusRelatorio);

        panelAcoesResultados.add(panelBotoes, BorderLayout.NORTH);

        // Tabela
        tblResultado = new JTable();
        scrollPaneResultado = new JScrollPane(tblResultado);
        panelAcoesResultados.add(scrollPaneResultado, BorderLayout.CENTER);

        mainPanel.add(panelAcoesResultados, BorderLayout.SOUTH);

        // --- Lógica de Eventos ---
        cmbTipoRelatorio.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                atualizarFiltrosVisiveis();
            }
        });

        btnGerar.addActionListener(e -> gerarRelatorio());
        btnExportarPDF.addActionListener(e -> exportarPDF());
    }

    private void atualizarFiltrosVisiveis() {
        String tipo = cmbTipoRelatorio.getSelectedItem().toString();
        panelFiltros.setVisible(true);
        btnGerar.setEnabled(true);

        // Oculta todos por padrão
        lblDataInicio.setVisible(false); txtDataInicio.setVisible(false);
        lblDataFim.setVisible(false); txtDataFim.setVisible(false);
        lblUnidade.setVisible(false); txtUnidade.setVisible(false);
        lblStatus.setVisible(false); cmbStatus.setVisible(false);

        switch (tipo) {
            case "Selecione...":
                panelFiltros.setVisible(false);
                btnGerar.setEnabled(false);
                break;
            case "Unidades":
            case "Moradores":
                lblUnidade.setVisible(true); txtUnidade.setVisible(true);
                lblStatus.setVisible(true); cmbStatus.setVisible(true);
                cmbStatus.setModel(new DefaultComboBoxModel<>(new String[]{"Todos", "Ativo", "Inativo"}));
                break;
            case "Taxas de Condomínio":
                lblDataInicio.setVisible(true); txtDataInicio.setVisible(true);
                lblDataFim.setVisible(true); txtDataFim.setVisible(true);
                lblUnidade.setVisible(true); txtUnidade.setVisible(true);
                lblStatus.setVisible(true); cmbStatus.setVisible(true);
                cmbStatus.setModel(new DefaultComboBoxModel<>(new String[]{"Todas", "Paga", "Pendente"}));
                break;
            case "Reservas":
            case "Manutenções":
                lblDataInicio.setVisible(true); txtDataInicio.setVisible(true);
                lblDataFim.setVisible(true); txtDataFim.setVisible(true);
                lblStatus.setVisible(true); cmbStatus.setVisible(true);
                cmbStatus.setModel(new DefaultComboBoxModel<>(new String[]{"Todas", "Agendada", "Concluída", "Cancelada"}));
                break;
        }
        panelFiltros.revalidate();
        panelFiltros.repaint();
    }

    private void gerarRelatorio() {
        lblStatusRelatorio.setVisible(false);
        btnExportarPDF.setEnabled(false);
        tblResultado.setModel(new DefaultTableModel()); // Limpa a tabela

        String tipoRelatorio = cmbTipoRelatorio.getSelectedItem().toString();
        // TODO: Chamar o DAL (ModuloConexao) para buscar os dados no banco usando os filtros.

        // Simulação de dados
        DefaultTableModel model = new DefaultTableModel();
        boolean hasData = false;

        switch (tipoRelatorio) {
            case "Moradores":
                model.setColumnIdentifiers(new String[]{"ID", "Nome", "Unidade", "Telefone", "Status"});
                model.addRow(new Object[]{"1", "João da Silva", "101A", "(11) 98765-4321", "Ativo"});
                model.addRow(new Object[]{"2", "Maria Oliveira", "202B", "(21) 91234-5678", "Ativo"});
                hasData = true;
                break;
            case "Taxas de Condomínio":
                model.setColumnIdentifiers(new String[]{"ID Taxa", "Unidade", "Valor", "Vencimento", "Status"});
                model.addRow(new Object[]{"T001", "101A", "R$ 500,00", "10/11/2025", "Paga"});
                model.addRow(new Object[]{"T002", "202B", "R$ 550,00", "10/11/2025", "Pendente"});
                hasData = true;
                break;
            // Adicionar outros casos de simulação aqui
        }

        if (hasData) {
            tblResultado.setModel(model);
            btnExportarPDF.setEnabled(true);
        } else {
            lblStatusRelatorio.setText("Nenhum registro encontrado para '" + tipoRelatorio + "'.");
            lblStatusRelatorio.setVisible(true);
        }
    }

    private void exportarPDF() {
        // TODO: Implementar lógica de exportação para PDF (ex: iText ou Apache PDFBox).
        JOptionPane.showMessageDialog(this, "Relatório exportado com sucesso! (Simulação)", "Exportação PDF", JOptionPane.INFORMATION_MESSAGE);
    }
}
