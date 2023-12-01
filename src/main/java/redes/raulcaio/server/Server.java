package redes.raulcaio.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import redes.raulcaio.tools.Ansi_colors;

import java.io.*;
import java.net.*;
import java.util.*;

import static redes.raulcaio.calculator.NetworkAddressCalculator.calculateNetworkAddress;
import static redes.raulcaio.calculator.NetworkAddressCalculator.calculateSubnetMask;

// Redes de computadores 2023/2
// Raul Souza
// Caio Furlan

public class Server {

    public static void main(String[] args) throws IOException {

        System.out.println("IP do servidor: " + InetAddress.getLocalHost().getHostAddress());

        ServerSocket serverSocket = null;
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\nPorta do servidor:");
        int serverPort = Integer.parseInt(stdIn.readLine());
        try {
            Ansi_colors colors = new Ansi_colors();
            serverSocket = new ServerSocket(serverPort);
            System.out.println("Servidor esperando por conexões...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(colors.getColor("green") + "\n→ Cliente conectado: " + socket.getInetAddress() + colors.getColor("default"));

                // Tratar a solicitação do cliente em uma thread separada
                new Thread(() -> {
                    handleClientRequest(socket);
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Não feche o socket aqui, pois isso encerraria a conexão para todos os clientes
        }
    }

    private static void handleClientRequest(Socket socket) {
        try {
            Ansi_colors colors = new Ansi_colors();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            ObjectMapper mapper = new ObjectMapper();

            // Ler a solicitação do cliente
            String clientRequest = inFromClient.readLine();
            JsonNode clientData = mapper.readTree(clientRequest);

            // Imprimir a solicitação do cliente
            System.out.println(colors.getColor("yellow") + "→ Solicitação do cliente: " + colors.getColor("default") + clientData);

            // Obter o número de redes e a quantidade de máquinas de cada rede
            int n_redes = clientData.get("n_redes").asInt();
            JsonNode redes = clientData.get("redes");


            Map<String, String> resposta = new HashMap<>();

            // Calcular a máscara e o endereço de rede para cada rede
            int currentNetworkIndex = 0;
            for (int i = 1; i <= n_redes; i++) {
                int maquinas = redes.get(String.valueOf(i)).get("maquinas").asInt();

                String networkAddress = calculateNetworkAddress("192.168.0.0", currentNetworkIndex);
                int subnetMask = calculateSubnetMask(maquinas);

                String enderecoRede = networkAddress + "/" + subnetMask;
                System.out.println("Network " + i + ": " + enderecoRede);
                resposta.put(String.valueOf(i), enderecoRede);

                // Move o index atual para o proximo endereço de rede disponível
                currentNetworkIndex += 1 << (32 - subnetMask);
            }

            // Enviar a resposta para o cliente
            String serverResponse = mapper.writeValueAsString(resposta);
            out.println(serverResponse);
            System.out.println(colors.getColor("green") + "→ Resposta enviada para o cliente: " + colors.getColor("default") + serverResponse);

            System.out.println("----------------------------------------------------------------------------------------------------");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Fechar o socket individualmente após o tratamento da solicitação
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}