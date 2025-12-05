"use client"

import { useEffect, useState } from 'react'; // Adicionado 'useState' aqui
import { User, Store } from 'lucide-react'; // Ícones para perfil e barracas
import { Button } from '@/components/ui/button';
import { useAuthStore } from '@/store/auth-store';
import { useRouter } from 'next/navigation';
import { product, profile, stock, tent } from '@/utils/types';

interface Message {
    type: 'error' | 'success';
    text: string;
}

// 5. Componente principal da página de Perfil (Agora o único exportado)
export default function Profile() {
    const router = useRouter();
    const { logout } = useAuthStore();

    const [userProfile, setUserProfile] = useState<any>(null);
    const [userTents, setUserTents] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
      const fetchData = async () => {
        try {
          const userId = localStorage.getItem("userId");

          if (!userId) {
            console.log("Usuário não logado");
            return;
          }

          const [profilesRes, tentsRes, productsRes] = await Promise.all([
            fetch(`${process.env.NEXT_PUBLIC_NGROK_URL}/usuarios`).then(res => res.json()),
            fetch(`${process.env.NEXT_PUBLIC_NGROK_URL}/tents`).then(res => res.json()),
            fetch(`${process.env.NEXT_PUBLIC_NGROK_URL}/prducts`).then(res => res.json())
          ]);
          
          const user = profilesRes.find((u: profile) => u.cpf === userId);
          let tents = tentsRes.filter((t: tent) => t.cpfHolder === user.cpf);
          
          const mapaDeProdutos = new Map(productsRes.map((p: product) => [p.code, p]));
          tents = tents.map((tent: tent) => ({
            ...tent,
            lista_de_itens: tent.items.map((item: stock) => {
              const detalhesDoProduto = mapaDeProdutos.get(item.product.code);
              
              if (detalhesDoProduto) {
                return {
                  ...detalhesDoProduto,
                  estoque: item.stockQuantity
                };
              }

              // Fallback quando os detalhes do produto não existem
              return {
                code: item.product.code,
                imagem: '',
                name: 'Produto desconhecido',
                description: '',
                price: 0,
                estoque: item.stockQuantity
              };
            })
          }));
          setUserProfile(user);
          setUserTents(tents);
        } catch (error) {
          console.error("Erro ao buscar dados", error);
        } finally {
          setLoading(false);
        }
      };

      fetchData();
    }, []);

    const handleButtonClick = () => {
      logout();
      router.push('/');
      return;
    };

    if (loading) return <div>Carregando perfil...</div>

    return (
        <div className="min-h-screen bg-gray-50 p-4 sm:p-8 flex justify-center">
        <div className="w-full max-w-4xl space-y-8">

            {/* --- Cartão de Perfil Principal --- */}
            <div className="bg-white p-8 rounded-xl shadow-2xl border border-gray-100 flex flex-col md:flex-row items-center md:items-start space-y-6 md:space-y-0 md:space-x-8">
            
            {/* Foto do Perfil */}
            <div className="flex-shrink-0">
                <img
                className="h-32 w-32 rounded-full object-cover border-4 border-indigo-200 shadow-md"
                src={userProfile.src}
                alt={`Foto de perfil de ${userProfile.name}`}
                onError={(e) => {
                    const target = e.target as HTMLImageElement;
                    target.src = "https://placehold.co/128x128/6366f1/FFFFFF/svg?text=JG"; // Fallback simples
                }}
                />
            </div>

            {/* Detalhes do Usuário */}
            <div className="text-center md:text-left flex-grow">
                <div className="flex items-center justify-center md:justify-start">
                    <User className="w-6 h-6 text-indigo-600 mr-2" />
                    <h1 className="text-3xl font-extrabold text-gray-900">{userProfile.name}</h1>
                </div>
                <p className="mt-1 text-lg text-indigo-600 font-medium">{userProfile.login}</p>
                <p className="mt-2 text-sm text-gray-500">
                    Gerenciador principal das barracas no mercado.
                </p>

                <Button
                    className="mt-4 px-4 py-2 bg-red-500 hover:bg-red-400 hover:cursor-pointer"
                    onClick={handleButtonClick}
                >
                    Logout
                </Button>
            </div>
            </div>

            {/* --- Lista de Barracas --- */}
            <div className="space-y-4">
            <h2 className="text-2xl font-bold text-gray-800 flex items-center">
                <Store className="w-6 h-6 text-pink-500 mr-2" />
                Minhas Barracas ({userTents.length})
            </h2>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {userTents.map((b: any) => (
                  <div
                    key={b.cod_barraca}
                    className="bg-white p-6 rounded-xl shadow-lg border-t-4 border-pink-500 transition duration-300 hover:shadow-xl hover:border-pink-600"
                  >
                    {/* <div className="space-y-2 text-sm text-gray-700">
                        <div className="flex items-center">
                            <MapPin className="w-4 h-4 text-gray-400 mr-2 flex-shrink-0" />
                            <span><span className="font-medium">Localização:</span> {p.localizacao}</span>
                        </div>
                        <div className="flex items-center">
                            <Tag className="w-4 h-4 text-gray-400 mr-2 flex-shrink-0" />
                            <span><span className="font-medium">Tipo:</span> {p.tipo}</span>
                        </div>
                    </div> */}
                    {b.lista_de_itens.map((p: any) => (
                      <div 
                        key={p.id}
                      >
                        <div className="flex items-center mb-4">
                        <Store className="w-6 h-6 text-pink-500 mr-3" />
                        <h3 className="text-xl font-semibold text-gray-900">{p.name}</h3>
                        </div>
                        <p className="text-gray-600 mb-3 text-sm italic">{p.description}</p>
                        <p>Estoque: {p.estoque}</p>
                      </div>
                  ))}
                  </div>
                ))}
            </div>
            
            {/* Mensagem de Status */}
            {/* {message && (
                <div className={`p-3 rounded-lg text-sm font-medium mt-6 ${
                message.type === 'error' ? 'bg-red-100 text-red-700 border border-red-300' : 'bg-green-100 text-green-700 border border-green-300'
                }`}>
                {message.text}
                </div>
            )} */}
            </div>
        </div>
        </div>
    );
  }