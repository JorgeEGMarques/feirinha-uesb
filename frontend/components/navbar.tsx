import Link from "next/link";

export const NavBar = () => {
  return (
    <nav className="sticky top-0 z-50 bg-white shadow">
      <div className="container mx-auto flex items-center justify-between px-4 py-4">
        <Link href="/" className="hover:text-blue-600">
          Feirinha UESB
        </Link>
        <div className="hidden md:flex space-x-6">
          <Link href={"/"}>Início</Link>
          <Link href={"/products"} className="hover:text-blue-600">Produtos</Link>
          <Link href={"/checkout"} className="hover:text-blue-600">Carrinho</Link>
        </div>
      </div>
    </nav>
  );
}