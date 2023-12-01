package redes.raulcaio.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import redes.raulcaio.tools.Ansi_colors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Client {

    public static void main(String[] args) throws IOException {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("IP do servidor:");
        String serverHostname = stdIn.readLine();
        System.out.println("Porta do servidor:");
        int serverPort = Integer.parseInt(stdIn.readLine());

        try {
            while (true) {
                try (Socket socket = new Socket(serverHostname, serverPort)) {
                    String jsonRequest = "";
                    PrintWriter outToServer = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    Ansi_colors colors = new Ansi_colors();

                    System.out.println("Digite a Quantidade de Redes:");
                    String qtdRedes = stdIn.readLine();

                    // Criar o JSON
                    ObjectMapper objectMapper = new ObjectMapper();

                    Map<String, Object> jsonMap = new HashMap<>();
                    jsonMap.put("n_redes", qtdRedes);

                    Map<String, Object> listaRedesMap = new HashMap<>();

                    for (int i = 1; i <= Integer.parseInt(qtdRedes); i++) {
                        System.out.println("Digite a quantidade de máquinas da Rede" + colors.getColor("cyan") + " " + i + colors.getColor("default") + ":");
                        int qtdMaquinas = Integer.parseInt(stdIn.readLine());

                        Map<String, Object> redeMap = new HashMap<>();
                        redeMap.put("maquinas", qtdMaquinas);
                        listaRedesMap.put(String.valueOf(i), redeMap);
                    }

                    jsonMap.put("redes", listaRedesMap);
                    jsonRequest = objectMapper.writeValueAsString(jsonMap);

                    // Enviar o JSON para o servidor
                    sendRequest(outToServer, jsonRequest);
                    System.out.println(colors.getColor("yellow") + "→ Pedido Enviado:" + colors.getColor("default") + jsonRequest);

                    // Receber resposta do servidor
                    String serverResponse = getResponse(inFromServer);
                    System.out.println(colors.getColor("green") + "→ Resposta do Servidor:" + colors.getColor("default") + serverResponse);

                    // Mapear o JSON para um objeto Java
                    objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(serverResponse);

                    System.out.println("-----------------------------------------------------------------------------");

                    // Exibir os endereços das redes
                    for (int i = 1; i <= jsonNode.size(); i++) {
                        String rede = jsonNode.get(String.valueOf(i)).asText();
                        System.out.println("Endereço da Rede " + colors.getColor("cyan") + i + colors.getColor("default") + ":");
                        System.out.println("↳ " + rede);
                        if (i < jsonNode.size()) {
                            System.out.println();
                        }
                    }

                    System.out.println("-----------------------------------------------------------------------------");
                    System.out.println(colors.getColor("green") + "→ Sucesso. Iniciando nova consulta." + colors.getColor("default"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getResponse(BufferedReader inFromServer) {
        Ansi_colors colors = new Ansi_colors();
        try {
            String serverResponse = inFromServer.readLine();
            return serverResponse;
        } catch (IOException e) {
            System.err.println(colors.getColor("red") + "Error ao ler mensagem do servidor: " + colors.getColor("default") + e.getMessage());
            return "";
        }
    }

    private static void sendRequest(PrintWriter outToServer, String jsonRequest) {
        outToServer.println(jsonRequest);
        outToServer.flush();
    }
}
