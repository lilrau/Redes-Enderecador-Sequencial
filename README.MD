
# 📍 Endereçador Sequencial

Projeto para a disciplina Redes de Computadores, cujo objetivo é determinar o endereço de rede sequencial conforme a quantidade de redes e máquinas.

O cliente deve fornecer as seguintes informações: número de redes e a quantidade de máquinas de cada rede. O servidor recebe estas informações e calcula a máscara e o endereço de cada rede.




## 📝 Exemplo

- Rede 1: 200 máquinas
    - Endereço: 192.168.0.0/24
- Rede 2: 100 máquinas
  - Endereço 192.168.1.0/25

## 📨 Protocolo de Troca de Dados

```
Pedido do Cliente:
{
  "n_redes": "2",
  "redes": {
  "1": {
      "maquinas": 200,
    },
  "2": {
      "maquinas": 100,
    }
  }
}


Resposta do Servidor:
{
  "1": "192.168.0.0/24",
  "2": "192.168.1.0/25"
}
```
## ⚙️ Dependências

- java 21.0.1 2023-10-17 LTS

```
<dependencies>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.15.0</version>
    </dependency>
</dependencies>
```

## 📜 Licença

Este projeto está sob a licença do MIT. Consulte a [LICENÇA](https://choosealicense.com/licenses/mit/) para obter mais informações.
<br><br>
[![GitHub](https://img.shields.io/github/license/lilrau/HigLimp)](<[MIT](https://choosealicense.com/licenses/mit/)>)