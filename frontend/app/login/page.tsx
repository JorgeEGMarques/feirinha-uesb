"use client"

import React, { useState } from 'react';
import { LogIn, Mail, Lock } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/store/auth-store';

interface Message {
  type: 'error' | 'success';
  text: string;
}

interface Profile {
  id: string,
  login: string,
  password: string
}

export default function Login() {
  const router = useRouter();
  const loginAction = useAuthStore((state) => state.login);
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [message, setMessage] = useState<Message | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setMessage(null);
    setIsLoading(true);

    if (!email || !password) {
      setMessage({ type: 'error', text: 'Por favor, preencha todos os campos.' });
      setIsLoading(false);
      return;
    }

    const profiles = await fetch('http://localhost:3000/profile')
    .then(response => response.json())
    .catch(error => console.error('Error', error));

    if (profiles.find((a: Profile) => a.login === email)) {
      let index = profiles.findIndex((a: Profile) => a.login === email);

      if (profiles[index].password === password) {
        loginAction(profiles[index].id)
        setMessage({ type: 'success', text: 'Login bem-sucedido! Redirecionando...' });
        router.push('/');
        return
      }
    }
    
    setMessage({ type: 'error', text: 'Email ou senha incorretos.' });
    setIsLoading(false);

  };

  const handleForgotPassword = (e: React.MouseEvent<HTMLAnchorElement>) => {
    e.preventDefault();
    if (typeof window !== 'undefined') {
      setMessage({ type: 'error', text: 'Pois lembre!' });
    }
  };

  return (
    <div className="flex items-center justify-center">
      <div className="w-full max-w-md bg-white rounded-xl shadow-2xl p-8 space-y-8 border border-gray-100 transition-all duration-300 hover:shadow-3xl">
        
        {/* Cabeçalho */}
        <div className="text-center">
          <LogIn className="w-12 h-12 mx-auto text-black-600 mb-3" />
          <h1 className="text-3xl font-extrabold text-gray-900">
            Entrar na Sua Conta
          </h1>
          <p className="mt-2 text-sm text-gray-500">
            Bem-vindo de volta! Por favor, insira suas credenciais.
          </p>
        </div>

        {/* Formulário de Login */}
        <form className="space-y-6" onSubmit={handleSubmit}>
          {/* Campo de Email */}
          <div>
            <label 
              htmlFor="email" 
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              Endereço de Email
            </label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Mail className="h-5 w-5 text-gray-400" />
              </div>
              <input
                id="email"
                name="email"
                type="email"
                autoComplete="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="appearance-none block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                placeholder="voce@exemplo.com"
                disabled={isLoading}
              />
            </div>
          </div>

          {/* Campo de Senha */}
          <div>
            <label 
              htmlFor="password" 
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              Senha
            </label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Lock className="h-5 w-5 text-gray-400" />
              </div>
              <input
                id="password"
                name="password"
                type="password"
                autoComplete="current-password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="appearance-none block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                placeholder="••••••••"
                disabled={isLoading}
              />
            </div>
          </div>

          {/* Opções (Esqueceu a senha) */}
          <div className="flex items-center justify-end">
            <div className="text-sm">
              <a 
                href="#" 
                className="font-medium text-black-600 hover:text-gray-500 transition duration-150 ease-in-out"
                onClick={handleForgotPassword} // Usando a nova função tipada
              >
                Esqueceu a senha?
              </a>
            </div>
          </div>
          
          {/* Mensagem de Status */}
          {message && (
            <div className={`p-3 rounded-lg text-sm font-medium ${
              message.type === 'error' ? 'bg-red-100 text-red-700 border border-red-300' : 'bg-green-100 text-green-700 border border-green-300'
            }`}>
              {message.text}
            </div>
          )}

          {/* Botão de Entrar */}
          <div className="max-w-md mx-auto">
            <Button
              type="submit"
              disabled={isLoading} // Desabilita o botão durante o carregamento
              variant="default"
              className="w-full"
            >
              {isLoading ? (
                <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
              ) : (
                'Entrar'
              )}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};