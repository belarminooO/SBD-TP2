package auth;

/**
 * Enumeração dos perfis de utilizador do sistema VetCare.
 */
public enum Role {
    GERENTE,      // Acesso total ao sistema
    VETERINARIO,  // Consulta clientes/animais, gere agendamentos e histórico
    CLIENTE       // Acesso apenas aos seus dados pessoais
}