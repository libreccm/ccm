-- switch the dependency from atoz to terms

-- switch the initializer dependency
update init_requirements
set    required_init = 'com.arsdigita.london.terms.Initializer',
       init = 'com.arsdigita.london.atoz.Initializer'
where  required_init = 'com.arsdigita.london.atoz.Initializer'
and    init = 'com.arsdigita.london.terms.Initializer';

-- switch the DomainProvider from terms.atoz to atoz.terms
update acs_objects
set    object_type = 'com.arsdigita.london.atoz.terms.DomainProvider',
       default_domain_class = 'com.arsdigita.london.atoz.terms.DomainProvider',
       display_name = 'com.arsdigita.london.atoz.terms.DomainProvider ' || object_id
where  object_type = 'com.arsdigita.london.terms.atoz.DomainProvider';


