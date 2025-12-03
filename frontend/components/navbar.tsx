"use client"

import Link from "next/link";
import { ShoppingCartIcon, Bars3Icon, XMarkIcon, UserIcon } from "@heroIcons/react/24/outline";
import { useCartStore } from "@/store/cart-store";
import { useAuthStore } from "@/store/auth-store";
import { useEffect, useState } from "react";
import { Button } from "./ui/button";

export const NavBar = () => {
  const [mobileOpen, setMobileOpen] = useState<boolean>(false);
  const { isLogged, checkLoginStatus, logout } = useAuthStore();
  const {items} = useCartStore();
  const cartCount = items.reduce((acc, item) => acc + item.quantity, 0);

  useEffect(() => {
    checkLoginStatus();
  }, [checkLoginStatus]);

  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth >= 768) {
        setMobileOpen(false);
      }
    };

    window.addEventListener("resize", handleResize);

    return () => window.removeEventListener("resize", handleResize);
  }, []);

  return (
    <nav className="sticky top-0 z-50 bg-white shadow">
      <div className="container mx-auto grid grid-cols-3 items-center w-full min-h-[50px] px-4 py-4">
        <Link href="/" className="hover:text-blue-600 justify-self-start p-2">
          Feirinha UESB
        </Link>
        <div className="hidden md:flex space-x-6 justify-self-center p-2">
          <Link href={"/"} className="hover:text-blue-600">Início</Link>
          <Link href={"/products"} className="hover:text-blue-600">Produtos</Link>
          <Link href={"/checkout"} className="hover:text-blue-600">Carrinho</Link>
          <Link href="/checkout" className="relative">
            <ShoppingCartIcon className="h-6 w-6" />
            { cartCount > 0 && (
              <span className="absolute -top-2 -right-2 flex h-5 w-5 items-center justify-center rounded-full bg-red-500 text-xs text-white">
                {cartCount}
              </span>
            )}
          </Link>
        </div>
        <div className="flex items-center space-x-4 justify-self-end p-2">
          <Button
            variant="ghost"
            onClick={() => setMobileOpen((prev) => !prev)}
            className="md:hidden"
          >
            {mobileOpen ? <XMarkIcon className="h-6 w-6" /> : <Bars3Icon className="h-6 w-6" />}
          </Button>
          {isLogged ?      
            <Link href={"/profile"} className="w-5 hover:text-blue-600">
              <UserIcon className="h-6 w-6"/>
            </Link>
            :
            <Link href={"/login"} className="w-5 hover:text-blue-600">
              Entrar
            </Link>
          }
        </div>
      </div>
      {mobileOpen && (
        <nav className="md:hidden bg-white shadow-md">
          {" "}
          <ul className="flex flex-col p-4 space-y-2">
            {" "}
            <li>
              <Link href="/" className="block hover:text-blue-600">Início</Link>
            </li>
            <li>
              <Link href="/products" className="block hover:text-blue-600">Produtos</Link>
            </li>
            <li>
              <Link  href="/checkout" className="block hover:text-blue-600">Carrinho</Link>
            </li>
          </ul>
        </nav>
      )}
    </nav>
  );
}