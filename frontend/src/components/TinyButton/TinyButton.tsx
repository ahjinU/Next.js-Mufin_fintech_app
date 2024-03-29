interface TinyButtonProps {
  label: string;
  isWarning?: boolean;
  onClick?: () => void;
}

export default function TinyButton({
  label,
  isWarning,
  ...props
}: TinyButtonProps) {
  return (
    <button
      className={`min-w-fit h-[2.4rem] px-[0.8rem] rounded-[0.8rem]
        text-custom-white custom-light-text ${
          isWarning
            ? 'bg-custom-red hover:bg-custom-dark-red'
            : 'bg-custom-purple hover:bg-custom-dark-purple'
        }`}
      {...props}
    >
      {label}
    </button>
  );
}
