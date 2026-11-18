import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class exercicio2_0 {

    private static double[] chuvas = new double[7];
    private static double[][] umidade = new double[4][4];
    private static boolean dadosCadastrados = false;

    private static final String[] DIAS = {
            "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo"
    };

    private static final String ARQUIVO_HISTORICO = "historico_chuvas.csv";
    private static final String ARQUIVO_TALHOES = "mapa_talhoes.csv";
    private static final String ARQUIVO_RELATORIO = "relatorio_fazenda.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            exibirMenuPrincipal();
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    cadastrarChuvas(scanner);
                    break;
                case 2:
                    cadastrarTalhoes(scanner);
                    break;
                case 3:
                    if (!dadosCadastrados) {
                        System.out.println("\n[Aviso] Cadastre os dados de chuva e talhões primeiro!");
                        break;
                    }
                    exibirMapaCampo();
                    break;
                case 4:
                    if (!dadosCadastrados) {
                        System.out.println("\n[Aviso] Cadastre os dados primeiro!");
                        break;
                    }
                    gerarRelatorioIrrigacao();
                    break;
                case 5:
                    consultarHistoricoChuvas();
                    break;
                case 6:
                    gerarDashboardClimatico();
                    break;
                case 7:
                    if (!dadosCadastrados) {
                        System.out.println("\n[Aviso] Cadastre os talhões primeiro (Opção 2)!");
                        break;
                    }
                    gerarRelatorioGerencial();
                    break;
                case 8:
                    System.out.println("\nEncerrando o AgroJava. Até logo!");
                    break;
                default:
                    System.out.println("\nOpção inválida! Tente novamente.");
            }
        } while (opcao != 8);

        scanner.close();
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n===== AGROJAVA - SISTEMA INTEGRADO =====");
        System.out.println("1) Cadastrar / Registrar Semana de Chuvas");
        System.out.println("2) Cadastrar / Mapear Umidade dos Talhões");
        System.out.println("3) Exibir Mapa do Campo (Matriz)");
        System.out.println("4) Relatório de Alertas de Irrigação");
        System.out.println("5) Consultar Histórico de Chuvas (CSV)");
        System.out.println("6) Dashboard Climático");
        System.out.println("7) Relatório Gerencial da Fazenda & Exportar (.txt)");
        System.out.println("8) Sair");
    }

    private static void cadastrarChuvas(Scanner scanner) {
        System.out.println("\n--- REGISTRO DE CHUVAS ---");
        for (int i = 0; i < 7; i++) {
            System.out.print("Digite o volume de chuva de " + DIAS[i] + " (em mm): ");
            chuvas[i] = scanner.nextDouble();
        }
        scanner.nextLine();

        dadosCadastrados = true;
        salvarHistoricoChuvas();
        System.out.println("\n[Sucesso] Chuvas cadastradas e salvas no histórico CSV!");
    }

    private static void salvarHistoricoChuvas() {
        double soma = 0;
        for (double c : chuvas) {
            soma += c;
        }
        double media = soma / 7;

        String dataHoje = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        File arquivo = new File(ARQUIVO_HISTORICO);
        boolean arquivoExiste = arquivo.exists();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_HISTORICO, true))) {
            if (!arquivoExiste) {
                bw.write("data,seg,ter,qua,qui,sex,sab,dom,media,total");
                bw.newLine();
            }

            StringBuilder sb = new StringBuilder();
            sb.append(dataHoje);
            for (double c : chuvas) {
                sb.append(",").append(c);
            }
            sb.append(String.format(",%.1f,%.1f", media, soma));

            bw.write(sb.toString());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("[Erro ao salvar histórico de chuvas]: " + e.getMessage());
        }
    }

    private static void consultarHistoricoChuvas() {
        System.out.println("\n--- HISTÓRICO DE CHUVAS (CSV) ---");
        File arquivo = new File(ARQUIVO_HISTORICO);
        if (!arquivo.exists()) {
            System.out.println("Nenhum histórico encontrado ainda.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_HISTORICO))) {
            String linha;
            boolean primeiro = true;
            while ((linha = br.readLine()) != null) {
                if (primeiro) {
                    primeiro = false;
                    continue;
                }
                String[] partes = linha.split(",");
                System.out.println("Semana: " + partes[0]);
                System.out.println("Média: " + partes[8] + " mm");
                System.out.println("Total: " + partes[9] + " mm");
                System.out.println("-----------------------------------");
            }
        } catch (IOException e) {
            System.out.println("[Erro ao ler histórico]: " + e.getMessage());
        }
    }

    private static void gerarDashboardClimatico() {
        System.out.println("\n===== DASHBOARD CLIMÁTICO =====");
        File arquivo = new File(ARQUIVO_HISTORICO);
        if (!arquivo.exists()) {
            System.out.println("Nenhum dado cadastrado para gerar o dashboard.");
            return;
        }

        int semanasCadastradas = 0;
        double volumeAcumuladoTotal = 0;
        double maiorChuvaTotal = -1;
        double menorChuvaTotal = Double.MAX_VALUE;

        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_HISTORICO))) {
            String linha;
            boolean primeiro = true;
            while ((linha = br.readLine()) != null) {
                if (primeiro) {
                    primeiro = false;
                    continue;
                }
                String[] partes = linha.split(",");
                double totalSemana = Double.parseDouble(partes[9]);

                semanasCadastradas++;
                volumeAcumuladoTotal += totalSemana;

                if (totalSemana > maiorChuvaTotal) {
                    maiorChuvaTotal = totalSemana;
                }
                if (totalSemana < menorChuvaTotal) {
                    menorChuvaTotal = totalSemana;
                }
            }

            double mediaGeral = semanasCadastradas > 0 ? volumeAcumuladoTotal / semanasCadastradas : 0;

            System.out.println("Semanas cadastradas: " + semanasCadastradas);
            System.out.printf("Volume acumulado: %.1f mm\n", volumeAcumuladoTotal);
            System.out.printf("Maior chuva (semanal): %.1f mm\n", maiorChuvaTotal);
            System.out.printf("Menor chuva (semanal): %.1f mm\n", menorChuvaTotal);
            System.out.printf("Média geral: %.1f mm\n", mediaGeral);

        } catch (IOException e) {
            System.out.println("[Erro ao processar dashboard]: " + e.getMessage());
        }
    }

    private static void cadastrarTalhoes(Scanner scanner) {
        System.out.println("\n--- UMIDADE DOS TALHÕES (Matriz 4x4) ---");
        for (int i = 0; i < umidade.length; i++) {
            for (int j = 0; j < umidade[i].length; j++) {
                System.out.print("Digite a umidade do talhão [" + i + "][" + j + "] (%): ");
                umidade[i][j] = scanner.nextDouble();
            }
        }
        scanner.nextLine();
        dadosCadastrados = true;
        salvarMapaTalhoes();
        System.out.println("\n[Sucesso] Talhões cadastrados e mapa salvo em CSV!");
    }

    private static void salvarMapaTalhoes() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_TALHOES))) {
            bw.write("talhao,umidade");
            bw.newLine();
            char linhaLetra = 'A';
            for (int i = 0; i < umidade.length; i++) {
                for (int j = 0; j < umidade[i].length; j++) {
                    String nomeTalhao = "" + linhaLetra + (j + 1);
                    bw.write(nomeTalhao + "," + umidade[i][j]);
                    bw.newLine();
                }
                linhaLetra++;
            }
        } catch (IOException e) {
            System.out.println("[Erro ao salvar mapa de talhões]: " + e.getMessage());
        }
    }

    private static void exibirMapaCampo() {
        System.out.println("\n--- MAPA DE UMIDADE DO CAMPO ---");
        for (int i = 0; i < umidade.length; i++) {
            for (int j = 0; j < umidade[i].length; j++) {
                System.out.printf("%.1f%%\t", umidade[i][j]);
            }
            System.out.println();
        }
    }

    private static String classificarTalhao(double valor) {
        if (valor < 20) {
            return "Crítico";
        } else if (valor <= 30) {
            return "Atenção";
        } else {
            return "Adequado";
        }
    }

    private static void gerarRelatorioIrrigacao() {
        System.out.println("\n===== ALERTAS DE IRRIGAÇÃO =====");
        boolean encontrouAlerta = false;
        char linhaLetra = 'A';
        int totalAlertas = 0;

        for (int i = 0; i < umidade.length; i++) {
            for (int j = 0; j < umidade[i].length; j++) {
                String status = classificarTalhao(umidade[i][j]);
                if (!status.equals("Adequado")) {
                    String nomeTalhao = "" + linhaLetra + (j + 1);
                    System.out.println(nomeTalhao + " - " + status + " (Umidade: " + umidade[i][j] + "%)");
                    encontrouAlerta = true;
                    totalAlertas++;
                }
            }
            linhaLetra++;
        }

        if (!encontrouAlerta) {
            System.out.println("Nenhum talhão necessita de irrigação urgente.");
        } else {
            System.out.println("Total de alertas identificados: " + totalAlertas);
        }
    }

    private static void gerarRelatorioGerencial() {
        System.out.println("\n===== RELATÓRIO DA FAZENDA =====");

        double somaUmidade = 0;
        int totalTalhoes = 16;
        int talhoesCriticos = 0;
        int talhoesAdequados = 0;

        double menorUmidade = Double.MAX_VALUE;
        double maiorUmidade = -1;
        String talhaoMaisSeco = "";
        String talhaoMaisUmido = "";

        char linhaLetra = 'A';
        for (int i = 0; i < umidade.length; i++) {
            for (int j = 0; j < umidade[i].length; j++) {
                double val = umidade[i][j];
                somaUmidade += val;
                String nome = "" + linhaLetra + (j + 1);

                String status = classificarTalhao(val);
                if (status.equals("Crítico")) {
                    talhoesCriticos++;
                } else if (status.equals("Adequado")) {
                    talhoesAdequados++;
                }

                if (val < menorUmidade) {
                    menorUmidade = val;
                    talhaoMaisSeco = nome + "-" + val + "%";
                }
                if (val > maiorUmidade) {
                    maiorUmidade = val;
                    talhaoMaisUmido = nome + "-" + val + "%";
                }
            }
            linhaLetra++;
        }

        double mediaGeralUmidade = somaUmidade / totalTalhoes;
        double percentualIrrigacao = ((double) (talhoesCriticos + countAtencao()) / totalTalhoes) * 100;

        System.out.printf("Media Geral: %.1f%%\n", mediaGeralUmidade);
        System.out.println("Talhoes Criticos: " + talhoesCriticos);
        System.out.println("Talhoes Adequados: " + talhoesAdequados);
        System.out.printf("Necessidade de Irrigacao: %.1f%%\n", percentualIrrigacao);
        System.out.println("Talhao mais seco: " + talhaoMaisSeco);
        System.out.println("Talhao mais umido: " + talhaoMaisUmido);

        String dataHoje = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_RELATORIO))) {
            bw.write("===== RELATORIO DA FAZENDA =====");
            bw.newLine();
            bw.write("Data: " + dataHoje);
            bw.newLine();
            bw.write(String.format("Media Geral: %.1f%%\n", mediaGeralUmidade));
            bw.write("Talhoes Criticos: " + talhoesCriticos);
            bw.newLine();
            bw.write("Talhoes Adequados: " + talhoesAdequados);
            bw.newLine();
            bw.write(String.format("Necessidade de Irrigacao: %.1f%%\n", percentualIrrigacao));
            bw.newLine();
            bw.write("Talhao mais seco: " + talhaoMaisSeco);
            bw.newLine();
            bw.write("Talhao mais umido: " + talhaoMaisUmido);
            System.out.println("\n[Sucesso] Relatório exportado com sucesso para '" + ARQUIVO_RELATORIO + "'!");
        } catch (IOException e) {
            System.out.println("[Erro ao exportar relatório]: " + e.getMessage());
        }
    }

    private static int countAtencao() {
        int count = 0;
        for (int i = 0; i < umidade.length; i++) {
            for (int j = 0; j < umidade[i].length; j++) {
                if (classificarTalhao(umidade[i][j]).equals("Atenção")) {
                    count++;
                }
            }
        }
        return count;
    }
}