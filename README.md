# Chat Multicast Java
## Desenvolvimento de Aplicações Móveis e Distribuídas
### Aluno: Lucas A. Gusmão

![image](https://user-images.githubusercontent.com/35220979/107996223-a35a3700-6fbe-11eb-8d4a-3912a9d1632f.png)
> Figura 1: Janela de chat

# 1. Introdução
Este trabalho consiste em uma aplicação de chat desenvolvida com a linguagem Java, utilizando o protocolo Multicast para a comunicação entre cliente e servidor. A GUI da aplicação foi desenvolvida com a biblioteca Swing do Java.

## 1.1 Instruções para executar a aplicação
1. Compilar e executar a classe Main.java (servidor)
2. Compilar e executar a classe Client.java (cliente)
3. Conectar as duas no mesmo IP multicast (o servidor pedirá o IP no Terminal, enquanto o cliente possui uma UI para a entrada do dado)

# 2. Requisitos
Os requisitos básicos para a aplicação são os seguintes:

1. O servidor deve gerenciar múltiplas salas de bate papo.

2. O cliente deve ser capaz de solicitar a lista de salas.

3. O cliente deve ser capaz de solicitar acesso à uma das salas de bate papo.

3. O servidor deve manter uma lista dos membros da sala.

4. O cliente deve ser capaz de enviar mensagens para a sala.

5. O cliente deve ser capaz de sair da sala de bate papo.

# 3. Arquitetura
Como foi mencionado na introdução, a comunicação da aplicação ocorre pelo protocolo Multicast. São enviadas requisições em formato de `String` que são interpretados tanto pelo cliente quanto pelo servidor.

## 3.1 Diagrama de Classes

Foi desenvolvido um diagrama de relações entre as classes:

![classDiagram](https://user-images.githubusercontent.com/35220979/107996285-cc7ac780-6fbe-11eb-84e4-ca6e014d9d9d.png)

> Figura 2: Diagrama de Classes

## 3.2 Padrão das Requisições

O padrão definido para as requisições foi o seguinte:

`Cabeçalho;Token;Payload;`

O cabeçalho das requisições segue um padrão `Identificador:Status`. O identificador é utilizado para determinar o que o servidor deve fazer, como `createRoom`, por exemplo. Foi utilizado também um campo de token, gerado aleatóriamente, para tratar de requisições duplicadas que ocorriam na rede e informar o cliente quando sua requisição foi finalizada (o cliente verifica se uma requisição de mesmo identificador e mesmo token com o estado success ou fail foi disparada pelo servidor)

## 3.3 Especificações das Requisições

A tabela abaixo contem as especificações de cada requisição, contendo seu identificador e seu payload:

| # | Identificador      | Payload          | Status Possíveis  | Response                  |
|---|--------------------|------------------|-------------------|---------------------------|
| 1 | createRoom         | -                | success           | room                      |
| 2 | showRooms          | -                | success           | rooms                     |
| 3 | joinRoom           | roomId, username | success, fail     | roomId, username          |
| 4 | leaveRoom          | roomId, username | success, username | roomId, username          |
| 5 | showRoomInfo       | roomId           | success           | room                      |
| 6 | sendMessage        | roomId, message  | success           | roomId, username, message |

> Tabela 1: Especificações de Requisições

