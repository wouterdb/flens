Name:           flens
Version:        0.0.1
Release:        2%{?dist}
Summary:        Flens monitoring framework 

License:        Apache 2.0 
URL:            https://dnetcode.cs.kuleuven.be/projects/flens
Source0:         
BuildArch:      noarch

BuildRequires:  mvn-local
Requires:       monarch-control
Requires:       java-grok
Requires:       elasticsearch

%description


%prep
%setup -q


%build
%mvn_build

%install
rm -rf $RPM_BUILD_ROOT
%mvn_install

%files
%doc



%changelog
* Mon Feb 17 2014 Bart Vanbrabant
- 
