import Image from "next/image";

export default function Profile() {
  return (
    <div className="m-auto w-800 h-500 border-2 border-solid rounded-2xl">
      <div className="flex">
        <Image
          src={ '/mclovin.webp' }
          alt={ 'foto de perfil' }
          width={200}
          height={200}
          objectFit="cover"
          className="group-hover:opacity-90 transition-opacity duration-300 rounded-t-lg"
        />

        <div>
          <h1>Nome: McLovin</h1>
          <h1>Idade: O suficiente pra comprar bebida</h1>
        </div>
      </div>
    </div>
  )
}