"use client"

import Link from "next/link";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUser } from "@fortawesome/free-regular-svg-icons";
import { faCartShopping } from "@fortawesome/free-solid-svg-icons";
import {ShoppingCartIcon, Bars3Icon, XMarkIcon} from "@heroIcons/react/24/outline";
import { useCartStore } from "@/store/cart-store";
import { useEffect, useState } from "react";
import { Button } from "./ui/button";

export const NavBar = () => {
  const [mobileOpen, setMobileOpen] = useState<boolean>(false);
  const {items} = useCartStore();
  const cartCount = items.reduce((acc, item) => acc + item.quantity, 0);

  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth >= 768) {
        setMobileOpen(false);
      }
    };

    window.addEventListener("resize", handleResize);

    return () => window.removeEventListener("resize", handleResize);
  }, [])

  return (
    <nav className="sticky top-0 z-50 bg-white shadow">
      <div className="container mx-auto flex items-center justify-between px-4 py-4">
        <Link href="/" className="hover:text-blue-600">
          Feirinha UESB
        </Link>
        <div className="hidden md:flex space-x-6">
          <Link href={"/"} className="hover:text-blue-600">Início</Link>
          <Link href={"/products"} className="hover:text-blue-600">Produtos</Link>
          <Link href={"/checkout"} className="hover:text-blue-600">Carrinho</Link>
        </div>
        <div className="flex items-center space-x-4">
          <Link href="/checkout" className="relative">
            <ShoppingCartIcon className="h-6 w-6" />
            { cartCount > 0 && (
              <span className="absolute -top-2 -right-2 flex h-5 w-5 items-center justify-center rounded-full bg-red-500 text-xs text-white">
                {cartCount}
              </span>
            )}
          </Link>
          <Button
            variant="ghost"
            onClick={() => setMobileOpen((prev) => !prev)}
            className="md:hidden"
          >
            {mobileOpen ? <XMarkIcon className="h-6 w-6" /> : <Bars3Icon className="h-6 w-6" />}
          </Button>
        </div>
        {/* <Link href={"/profile"} className="w-5 hover:text-blue-600">
          <FontAwesomeIcon icon={faUser} />
        </Link> */}
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