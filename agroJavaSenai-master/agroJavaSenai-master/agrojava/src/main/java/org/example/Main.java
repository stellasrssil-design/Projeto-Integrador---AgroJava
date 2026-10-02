import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        double[] chuvas = new double[7];
        double[][] umidade = new double[4][4];

        String[] dias = {
                "Segunda",
                "Terça",
                "Quarta",
                "Quinta",
                "Sexta",
                "Sábado",
                "Domingo"
        };

        boolean dadosCadastrados = false;
        int opcao;

        do {
            System.out.println("\n===== AGROJAVA =====");
            System.out.println("1) Cadastrar Dados");
            System.out.println("2) Exibir Mapa do Campo");
            System.out.println("3) Relatório de Alertas de Irrigação");
            System.out.println("4) Sair");
            System.out.print("Escolha uma opção: ");

            opcao = scanner.nextInt();

            switch (opcao) {

                case 1:
                    System.out.println("\n--- REGISTRO DE CHUVAS ---");

                    for (int i = 0; i < 7; i++) {
                        System.out.print("Digite o volume de chuva de "
                                + dias[i] + " (em mm): ");
                        chuvas[i] = scanner.nextDouble();
                    }

                    System.out.println("\n--- UMIDADE DOS TALHÕES ---");

                    for (int i = 0; i < umidade.length; i++) {
                        for (int j = 0; j < umidade[i].length; j++) {

                            System.out.print("Digite a umidade do talhão ["
                                    + i + "][" + j + "] (%): ");

                            umidade[i][j] = scanner.nextDouble();
                        }
                    }

                    dadosCadastrados = true;

                    System.out.println("\nDados cadastrados com sucesso!");

                    break;

                case 2:

                    if (!dadosCadastrados) {
                        System.out.println("\nCadastre os dados primeiro! utiize  opção 1.");
                        break;
                    }

                    System.out.println("\n--- MAPA DE UMIDADE DO CAMPO ---");

                    for (int i = 0; i < umidade.length; i++) {

                        for (int j = 0; j < umidade[i].length; j++) {
                            System.out.printf("%.1f%%\t", umidade[i][j]);
                        }

                        System.out.println();
                    }

                    break;

                case 3:

                    if (!dadosCadastrados) {
                        System.out.println("\nCadastre os dados primeiro! utilize a opcao 1.");
                        break;
                    }

                    System.out.println("\n--- RELATÓRIO DE ALERTAS DE IRRIGAÇÃO ---");

                    double somaChuva = 0;

                    for (int i = 0; i < 7; i++) {
                        somaChuva += chuvas[i];
                    }

                    double mediaChuva = somaChuva / 7;

                    int maiorChuva = 0;

                    for (int i = 1; i < 7; i++) {

                        if (chuvas[i] > chuvas[maiorChuva]) {
                            maiorChuva = i;
                        }
                    }

                    System.out.printf("Média semanal de chuva: %.2f mm%n",
                            mediaChuva);

                    System.out.printf("Dia com maior índice de chuva: %s (%.2f mm)%n",
                            dias[maiorChuva],
                            chuvas[maiorChuva]);

                    System.out.println("\nTalhões que necessitam de irrigação:");

                    boolean encontrouAlerta = false;

                    for (int i = 0; i < umidade.length; i++) {

                        for (int j = 0; j < umidade[i].length; j++) {

                            if (umidade[i][j] < 30) {

                                System.out.printf(
                                        "Talhão [%d][%d] - Umidade: %.1f%%%n",
                                        i, j, umidade[i][j]
                                );

                                encontrouAlerta = true;
                            }
                        }
                    }

                    if (!encontrouAlerta) {
                        System.out.println(
                                "Nenhum talhão necessita de irrigação."
                        );
                    }

                    break;
                case 4:
                    System.out.println("\nEncerrando o AgroJava...");
                    break;

                default:
                    System.out.println("\nOpção inválida!");
            }

        } while (opcao != 4);

        scanner.close();
    }
}