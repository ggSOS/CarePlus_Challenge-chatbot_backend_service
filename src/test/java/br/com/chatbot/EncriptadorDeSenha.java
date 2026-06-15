package br.com.chatbot;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncriptadorDeSenha {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("senha123");
        System.out.println(hash);
    }
}
