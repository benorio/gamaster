package br.edu.gama.gamaster;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.edu.gama.gamaster.model.CartaoCredito;
import br.edu.gama.gamaster.model.Cliente;
import br.edu.gama.gamaster.model.Conta;
import br.edu.gama.gamaster.model.ContaCorrente;
import br.edu.gama.gamaster.model.ContaEspecial;
import br.edu.gama.gamaster.model.Movimentacao;
import br.edu.gama.gamaster.service.GerenciaContas;

@SpringBootApplication
public class GamasterApplication {
	public static List<Conta> contaList = new ArrayList<>();
	public static final String AGENCIA = "1500";

	public static void main(String[] args) {
		// Comentado para o Spring não tentar fazer a conexão com o banco por enquanto
//		SpringApplication.run(GamasterApplication.class, args);
		iniciar();
	}

	public static void iniciar() {
		String opcao = "";
		Scanner scanner = new Scanner(System.in);
		while (!opcao.equalsIgnoreCase("3")) {
			System.out.println("======== GAMASTER BANKING ========");
			System.out.println("[1] - Criar Conta\n" + "[2] - Acessar Conta\n" + "[3] - Encerrar");
			System.out.printf("Digite a opção desejada: ");

			opcao = scanner.next();

			switch (opcao) {
			case "1":
				criarConta();
				break;
			case "2":
				acessarConta();
				break;
			case "3":
				System.out.println("Obrigado por usar o sistema!");
				break;
			default:
				System.out.println("Opção Inválida");
				break;
			}
		}
		scanner.close();
	}

	public static void criarConta() {
		Cliente cliente = new Cliente();
		Conta conta = null;

		cliente.setNome(campoCadastro("Digite o nome do cliente: "));
		cliente.setCnpjCpf(campoCadastro("Digite o CPF/CNPJ do cliente: "));
		cliente.setTelefone(campoCadastro("Digite o telefone do cliente: "));

		if (cliente.getCnpjCpf().length() == 14) {
			conta = new ContaEspecial(BigDecimal.ZERO, AGENCIA, UUID.randomUUID().toString(), new CartaoCredito(),
					cliente, BigDecimal.valueOf(10000));
		} else {
			conta = new ContaCorrente(BigDecimal.ZERO, AGENCIA, UUID.randomUUID().toString(), new CartaoCredito(),
					cliente);
		}
		// testes de saldo
		conta.depositar(BigDecimal.valueOf(10123));

		contaList.add(conta);
		// main(new String[] { "2" });
	}

	public static Conta buscarConta(String cpfCnpj) {
		Optional<Conta> contaEncontrada = contaList.stream()
				.filter((conta) -> conta.getCliente().getCnpjCpf().equalsIgnoreCase(cpfCnpj)).distinct().findFirst();

		return contaEncontrada.orElse(null);
	}

	public static String campoCadastro(String texto) {
		String campo;
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.printf(texto);
			campo = scanner.nextLine();
			if (!campo.isEmpty() && campo != null) {
				break;
			}
		}
		return campo;
	}

	public static void acessarConta() {
		System.out.println("\n\n\n======== GAMASTER BANKING ========");
		System.out.print("Digite o CPF/CNPJ para acessar a conta: ");
		Scanner sc = new Scanner(System.in);
		String cpfCnpj = sc.nextLine();
		Conta conta = buscarConta(cpfCnpj);

		if (conta != null) {
			Boolean usuarioLogado = true;
			while (usuarioLogado) {
				System.out.println("[1] - Consultar Saldo\n" + "[2] - Consultar Extrato\n" + "[3] - Realizar Depósito\n"
						+ "[4] - Realizar Saque\n" + "[5] - Realizar Tranferência\n" + "[6] - Sair");
				System.out.print("\nDigite a opção desejada: ");
				int opcao = sc.nextInt();

				switch (opcao) {
				case 1:
					System.out.printf("Saldo da Conta: R$ %.2f%n", GerenciaContas.consultarSaldo(conta));
					break;
				case 2:
					List<Movimentacao> movimentacoes = GerenciaContas.consultarExtrato(conta);
					movimentacoes.forEach(System.out::println);
					break;
				case 3:
					System.out.print("Digite o valor a ser depositado: ");
					GerenciaContas.inserir(conta, sc.nextBigDecimal());
					break;
				case 4:
					// TODO Implementar Saque
					//acessarConta();
					break;
				case 5:
					// TODO Implementar Transferência
					//acessarConta();
					break;
				case 6:
					usuarioLogado = false;
					break;
				default:
					System.out.println("Opção Inválida!");
				}
			}
		}
	}
}
