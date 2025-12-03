import { create } from 'zustand';

interface AuthState {
  isLogged: boolean;
  userId: string | null;
  login: (id: string) => void;
  logout: () => void;
  checkLoginStatus: () => void; // Para verificar ao carregar a página
}

export const useAuthStore = create<AuthState>((set) => ({
  isLogged: false,
  userId: null,

  login: (id: string) => {
    // Salva no localStorage
    localStorage.setItem("logged", "true");
    localStorage.setItem("userId", id);
    // Atualiza o estado global (reagindo na hora)
    set({ isLogged: true, userId: id });
  },

  logout: () => {
    localStorage.removeItem("logged");
    localStorage.removeItem("userId");
    set({ isLogged: false, userId: null });
  },

  checkLoginStatus: () => {
    // Sincroniza com o localStorage ao iniciar a app
    const logged = localStorage.getItem("logged") === "true";
    const userId = localStorage.getItem("userId");
    if (logged) {
        set({ isLogged: true, userId });
    } else {
        set({ isLogged: false, userId: null });
    }
  }
}));