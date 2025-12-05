"use client"

import React, { useState } from 'react';
import { UserPlus, Mail, Lock, User, Phone, FileText, ArrowLeft } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { useRouter } from 'next/navigation';

interface Message {
  type: 'error' | 'success';
  text: string;
}

export default function Register() {
  const router = useRouter();
  
  const [formData, setFormData] = useState({
    nome: '',
    cpf: '',
    telefone: '',
    email: '',
    senha: ''
  });

  const [message, setMessage] = useState<Message | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value
    }));
  };

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setMessage(null);
    setIsLoading(true);

    if (!formData.nome || !formData.cpf || !formData.telefone || !formData.email || !formData.senha) {
      setMessage({ type: 'error', text: 'Por favor, preencha todos os campos.' });
      setIsLoading(false);
      return;
    }

    try {
      const baseUrl = process.env.NEXT_PUBLIC_NGROK_URL || process.env.NGROK_URL;

      const response = await fetch(`${baseUrl}/usuarios`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'ngrok-skip-browser-warning': 'true',
        },
        body: JSON.stringify({
          nome: formData.nome,
          cpf: formData.cpf,
          telefone: formData.telefone,
          email: formData.email,
          senha: formData.senha,
          fotoPerfil: null
        })
      });

      if (!response.ok) {
        throw new Error(`Erro HTTP: ${response.status}`);
      }

      const data = await response.json();
      console.log("Cadastro sucesso:", data);
      
      setMessage({ type: 'success', text: 'Conta criada com sucesso! Redirecionando...' });
      
      setTimeout(() => {
        router.push('/login'); 
      }, 2000);

    } catch (error) {
      console.error('Erro no cadastro:', error);
      setMessage({ type: 'error', text: 'Erro ao criar conta. Tente novamente.' });
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-50 p-4">
      <div className="w-full max-w-md bg-white rounded-xl shadow-2xl p-8 space-y-6 border border-gray-100 transition-all duration-300 hover:shadow-3xl">
        
        {/* Cabeçalho */}
        <div className="text-center">
          <UserPlus className="w-12 h-12 mx-auto text-indigo-600 mb-3" />
          <h1 className="text-3xl font-extrabold text-gray-900">
            Criar Nova Conta
          </h1>
          <p className="mt-2 text-sm text-gray-500">
            Preencha seus dados para começar a usar.
          </p>
        </div>

        {/* Formulário */}
        <form className="space-y-4" onSubmit={handleSubmit}>
          
          {/* Nome */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Nome Completo</label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <User className="h-5 w-5 text-gray-400" />
              </div>
              <input
                name="nome"
                type="text"
                required
                value={formData.nome}
                onChange={handleChange}
                className="appearance-none block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                placeholder="Seu nome"
                disabled={isLoading}
              />
            </div>
          </div>

          {/* CPF */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">CPF</label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <FileText className="h-5 w-5 text-gray-400" />
              </div>
              <input
                name="cpf"
                type="text"
                required
                value={formData.cpf}
                onChange={handleChange}
                className="appearance-none block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                placeholder="000.000.000-00"
                disabled={isLoading}
              />
            </div>
          </div>

          {/* Telefone */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Telefone</label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Phone className="h-5 w-5 text-gray-400" />
              </div>
              <input
                name="telefone"
                type="tel"
                required
                value={formData.telefone}
                onChange={handleChange}
                className="appearance-none block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                placeholder="(00) 00000-0000"
                disabled={isLoading}
              />
            </div>
          </div>

          {/* Email */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Mail className="h-5 w-5 text-gray-400" />
              </div>
              <input
                name="email"
                type="email"
                autoComplete="email"
                required
                value={formData.email}
                onChange={handleChange}
                className="appearance-none block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                placeholder="voce@exemplo.com"
                disabled={isLoading}
              />
            </div>
          </div>

          {/* Senha */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Senha</label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Lock className="h-5 w-5 text-gray-400" />
              </div>
              <input
                name="senha"
                type="password"
                required
                value={formData.senha}
                onChange={handleChange}
                className="appearance-none block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                placeholder="••••••••"
                disabled={isLoading}
              />
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

          {/* Botões */}
          <div className="space-y-3 pt-2">
            <Button
              type="submit"
              disabled={isLoading}
              className="w-full bg-indigo-600 hover:bg-indigo-700 text-white"
            >
              {isLoading ? 'Cadastrando...' : 'Cadastrar'}
            </Button>

            <Button
              type="button"
              variant="ghost"
              className="w-full text-gray-600 hover:text-indigo-600"
              onClick={() => router.push('/login')}
            >
              <ArrowLeft className="w-4 h-4 mr-2" /> Voltar para o Login
            </Button>
          </div>

        </form>
      </div>
    </div>
  );
}