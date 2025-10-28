{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    flake-parts.url = "github:hercules-ci/flake-parts";
  };

  outputs = {
    self,
    flake-parts,
    ...
  } @ inputs:
    flake-parts.lib.mkFlake {inherit inputs;} {
      systems = ["x86_64-linux"];

      perSystem = {
        config,
        lib,
        pkgs,
        system,
        ...
      }: let
        javaVersion = 25;

        jdk = pkgs.javaPackages.compiler.temurin-bin."jdk-${toString javaVersion}";
        gradle = pkgs.gradle_9.override {
            javaToolchains = [
                jdk
                pkgs.temurin-bin
            ];
        };

        pythonPackages = with pkgs; [
            virtualenv
            python312Packages.pip
            python312
        ];
       in {
         devShells.default = pkgs.mkShell {
           name = "JDA-Commands";
           packages = with pkgs; [git findutils ] ++ pythonPackages ++ [ gradle jdk ];
           NIX_JDK = jdk;
         };
       };
    };
}